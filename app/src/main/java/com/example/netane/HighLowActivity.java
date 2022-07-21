package com.example.netane;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@RequiresApi(api = Build.VERSION_CODES.N)

public class HighLowActivity extends AppCompatActivity {
    // List
    List<List<String>> CardList;
    List<Integer> PlayerPoint;
    // 変数
    String TAG, AnswerStr, ButtonTag, ResultStr;
    int POINT, SLEEP_TIME, FocusColor,NoneFocusColor, DiffX, DiffY,
            player, CorrectId, MistakeId, DrawId;
    // View
    androidx.constraintlayout.widget.ConstraintLayout screen;
    TextView header, center_tv, p1tv, p2tv, p1Point, p2Point ,orText;
    Button LowButton, HighButton;
    ImageView LeftCard, RightCard1, RightCard2, result;
    // Animator
    AnimatorSet ForwardAnimator, ReverseAnimator;
    ObjectAnimator F_translationX, F_translationY, F_scaleX, F_scaleY,
            R_translationX, R_translationY, R_scaleX, R_scaleY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlow);
        // 変数セット
        TAG = "HighLow_Activity";
        POINT = 1;
        player = 1;
        SLEEP_TIME = 1000;
        FocusColor = Color.CYAN; /* 手番プレイヤー色 */
        NoneFocusColor = Color.TRANSPARENT;

        // viewをセット
        screen = findViewById(R.id.HighLow_gameScreen);
        header = findViewById(R.id.HighLow_header);
        center_tv = findViewById(R.id.HighLow_center_tv);
        result = findViewById(R.id.HighLow_result);
        p1tv = findViewById(R.id.HighLow_player1);
        p2tv = findViewById(R.id.HighLow_player2);
        p1Point = findViewById(R.id.HighLow_player1_point);
        p2Point = findViewById(R.id.HighLow_player2_point);
        orText = findViewById(R.id.HighLow_orText);

        LowButton = findViewById(R.id.HighLow_low_button);
        HighButton = findViewById(R.id.HighLow_high_button);

        LeftCard = findViewById(R.id.HighLow_leftCard);
        RightCard1 = findViewById(R.id.HighLow_rightCard1);
        RightCard2 = findViewById(R.id.HighLow_rightCard2);

        // DrawableIDをセット
        CorrectId = getResources().getIdentifier("correct", "drawable", getPackageName());
        MistakeId = getResources().getIdentifier("mistake", "drawable", getPackageName());
        DrawId = getResources().getIdentifier("draw", "drawable", getPackageName());

        // 初期化
        init();
    }

    // 戻る
    public void tapBack(View view) {
        Intent intent = new Intent(HighLowActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // init
    public void init(){
        // 変数初期化
        PlayerPoint = new ArrayList<Integer>(Arrays.asList(0 , 0));

        // viewをリセット
        turnViews(false);

        // cardをセット
        selectCard();

        // game開始
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ゲーム画面をセット
                turnViews(true);
                playerChange(2); /* player名を光らせる */

                // Animatorをセット
                setAnimation();

                // 最初のカードをセットする
                showCard(RightCard1, CardList.get(0));

                // 表示順(重なり方)を指定
                RightCard1.bringToFront();
                result.bringToFront();

                // 次のカードを裏にセット
                showCard(RightCard2, CardList.get(1));
            }
        });
    }

    // resetView
    public void turnViews(boolean bool){
        if (bool){
            // game開始時の表示
            header.setVisibility(View.VISIBLE);
            result.setVisibility(View.INVISIBLE);
            p1tv.setVisibility(View.VISIBLE);
            p2tv.setVisibility(View.VISIBLE);
            p1Point.setVisibility(View.VISIBLE);
            p2Point.setVisibility(View.VISIBLE);
            orText.setVisibility(View.VISIBLE);
            LowButton.setVisibility(View.VISIBLE);
            HighButton.setVisibility(View.VISIBLE);
            LeftCard.setVisibility(View.INVISIBLE);
            RightCard1.setVisibility(View.VISIBLE);
            RightCard2.setVisibility(View.INVISIBLE);

            screen.setClickable(false);
            center_tv.setVisibility(View.INVISIBLE);
        }else{
            // center_tv以外を非表示
            header.setVisibility(View.INVISIBLE);
            result.setVisibility(View.INVISIBLE);
            p1tv.setVisibility(View.INVISIBLE);
            p2tv.setVisibility(View.INVISIBLE);
            p1Point.setVisibility(View.INVISIBLE);
            p2Point.setVisibility(View.INVISIBLE);
            orText.setVisibility(View.INVISIBLE);
            LowButton.setVisibility(View.INVISIBLE);
            HighButton.setVisibility(View.INVISIBLE);
            LeftCard.setVisibility(View.INVISIBLE);
            RightCard1.setVisibility(View.INVISIBLE);
            RightCard2.setVisibility(View.INVISIBLE);

            screen.setClickable(true);
            center_tv.setVisibility(View.VISIBLE);
            center_tv.setText("Tap screen to start");
        }
    }

    // 表示する順にカードリストを作成
    public void selectCard() {
        // cardリストを初期化
        CardList = new ArrayList<>();
        for (int num : IntStream.range(0, 2).boxed().collect(Collectors.toList())) {
            for (String suit : new String[]{"heart", "diamond", "clover", "spade"}) {
                CardList.add(new ArrayList<String>(Arrays.asList(suit , String.valueOf(num+1))));
            }
        }
        Collections.shuffle(CardList);
        Log.d(TAG, String.valueOf(CardList));
    }

    public void showCard(ImageView image, List<String> card){
        String suit = card.get(0);
        String num = card.get(1);
        int cardId = getResources().getIdentifier(suit + num, "drawable", getPackageName());
        image.setImageResource(cardId);
    }

    public String checkAnswer(){
        int current_num = Integer.parseInt(CardList.get(0).get(1));
        int next_num = Integer.parseInt(CardList.get(1).get(1));
        if(current_num < next_num) return "high";
        else if(current_num > next_num) return "low";
        else return "draw";
    }

    // tapHighLow
    public void tapHighLow(View view){
        ButtonTag = view.getTag().toString();
        AnswerStr = checkAnswer();

        // 正解のとき
        if (ButtonTag.equals(AnswerStr)){
            result.setImageResource(CorrectId);
            addPoint(player, POINT);
        }
        // 同じ数字のとき
        else if (AnswerStr.equals("draw")){
            result.setImageResource(DrawId);
        }
        // 不正解のとき
        else{
            result.setImageResource(MistakeId);
        }
        HighButton.setVisibility(View.INVISIBLE);
        LowButton.setVisibility(View.INVISIBLE);
        orText.setVisibility(View.INVISIBLE);
        result.setVisibility(View.VISIBLE);

        // 次のカード
        doNext();

        // playerチェンジ
        playerChange(player);
    }

    public void doNext(){
        // 裏のカードを見えるように設定
        RightCard2.setVisibility(View.VISIBLE);
        // anim開始
        ForwardAnimation();

        // ForwardAnimationが終了したら
        ForwardAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                // RightCardを戻す
                ReverseAnimation();

                // 表示済のカードを左にセット
                showCard(LeftCard, CardList.get(0));
                LeftCard.setVisibility(View.VISIBLE);

                // 表示済みのカードをlistから削除
                CardList.remove(0);

                if(CardList.size() == 1){
                    // 終了
                    makeResult();
                    return;
                }

                // カードをセット
                showCard(RightCard1, CardList.get(0));
                Log.d("Card", String.valueOf(CardList.get(0)));

                // 次のカードを裏にセット
                showCard(RightCard2, CardList.get(1));
                Log.d("NextCard", String.valueOf(CardList.get(1)));

                // ボタン類を再表示
                HighButton.setVisibility(View.VISIBLE);
                LowButton.setVisibility(View.VISIBLE);
                orText.setVisibility(View.VISIBLE);
                result.setVisibility(View.INVISIBLE);
            }
        });
    }

    // 正解時のプレイヤーへの加算
    public void addPoint(int player_num, int point){
        /*引数１：加算するプレイヤー文字列
         * 引数２：加算するポイント数*/
        // データ上でポイントを加算
        PlayerPoint.set(player_num-1, PlayerPoint.get(player_num-1) + point);
        // view上でポイントを加算
        p1Point.setText(String.valueOf(PlayerPoint.get(0)));
        p2Point.setText(String.valueOf(PlayerPoint.get(1)));
    }

    public void playerChange(int player_num){
        if(player_num == 1){
            player = 2;
            p1tv.setBackgroundColor(NoneFocusColor);
            p2tv.setBackgroundColor(FocusColor);
        }else{
            player = 1;
            p1tv.setBackgroundColor(FocusColor);
            p2tv.setBackgroundColor(NoneFocusColor);
        }
        header.setText("Player " + player);
    }

    public void makeResult(){
        // 表示をリセット
        turnViews(false);
        // 勝敗
        if (Objects.equals(PlayerPoint.get(0), PlayerPoint.get(1))){
            ResultStr = "Draw (∩´﹏`∩)" + "\n\nPoint\n\nPlayer1: " + PlayerPoint.get(0) + "\n" +
                    "Player2: " + PlayerPoint.get(1) + "\n\nTap screen to continue";
        }else{
            // リスト内の最大値を求める
            int max = Collections.max(PlayerPoint);
            // 最大値を与えているもののインデックスを求める
            int maxIndex = PlayerPoint.indexOf(max);
            player = maxIndex + 1;
            ResultStr = "Winner: Player" + player + "  ٩(ˊᗜˋ*)و\n\nPoint\n\nPlayer1: " + PlayerPoint.get(0) + "\n" +
                    "Player2: " + PlayerPoint.get(1) + "\n\nTap screen to continue";
        }
        // リザルトテキストの設定
        center_tv.setText(ResultStr);
        // nextGameの準備
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                init();
            }
        });
    }

    public void setAnimation(){
        //  getLocationOnScreenは画面上の絶対座標，
        int[] arrayL = new int[2];
        int[] arrayR = new int[2];
        LeftCard.getLocationOnScreen(arrayL);
        RightCard1.getLocationOnScreen(arrayR);
        Point LeftCardPoint = new Point(arrayL[0], arrayL[1]);
        Point RightCardPoint = new Point(arrayR[0], arrayR[1]);

        // cardの座標
        int LeftCardX = LeftCardPoint.x;
        int LeftCardY = LeftCardPoint.y;
        int RightCardX = RightCardPoint.x;
        int RightCardY = RightCardPoint.y;

        // 移動量を計算
        DiffX = LeftCardX - RightCardX - LeftCard.getWidth() / 2;
        DiffY = LeftCardY - RightCardY - LeftCard.getHeight() / 2;

        // ForwardAnimator用のObjectAnimatorをセット
        F_translationX = ObjectAnimator.ofFloat(RightCard1, "translationX", DiffX);
        F_translationY = ObjectAnimator.ofFloat(RightCard1, "translationY", DiffY);
        F_scaleX = ObjectAnimator.ofFloat(RightCard1, "scaleX", 1.0f, 0.5f);
        F_scaleY = ObjectAnimator.ofFloat(RightCard1, "scaleY", 1.0f, 0.5f);

        // ReverseAnimator用のObjectAnimatorをセット
        R_translationX = ObjectAnimator.ofFloat(RightCard1, "translationX", 0);
        R_translationY = ObjectAnimator.ofFloat(RightCard1, "translationY", 0);
        R_scaleX = ObjectAnimator.ofFloat(RightCard1, "scaleX", 0.5f, 1.0f);
        R_scaleY = ObjectAnimator.ofFloat(RightCard1, "scaleY", 0.5f, 1.0f);
    }

    public void ForwardAnimation(){
        // Animatorをセット
        ForwardAnimator = new AnimatorSet();
        ForwardAnimator.playTogether(F_translationX, F_translationY, F_scaleX, F_scaleY);
        ForwardAnimator.setDuration(500);
        ForwardAnimator.start();
    }

    public void ReverseAnimation(){
        // Animatorをセット
        ReverseAnimator = new AnimatorSet();
        ReverseAnimator.playTogether(R_translationX, R_translationY, R_scaleX, R_scaleY);
        ReverseAnimator.setDuration(0);
        ReverseAnimator.start();
    }

    /**
     * dpからpixelへの変換
     * @param dp density pixel
     * @param context context(this)
     * @return float pixel
     */
    public static float convertDp2Px(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }

    /**
     * pixelからdpへの変換
     * @param px pixel
     * @param context context(this)
     * @return float dp
     */
    public static float convertPx2Dp(int px, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / metrics.density;
    }
}