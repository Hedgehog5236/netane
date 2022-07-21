package com.example.netane;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@RequiresApi(api = Build.VERSION_CODES.N)

public class ConcentrationActivity extends AppCompatActivity {
    // 変数
    String TAG, ResultStr;
    List< List<String> > CardList, selectCardList, selectedCards;
    List<String> SuitList, cardBackSide, sbList, TempCard;
    List<Integer> PlayerPoint, NumList;
    List<ImageButton> CardButtonList, TempCardsButton;
    int NUM_PLAYER, NUM_CARD, POINT, SLEEP_TIME, FocusColor, NoneFocusColor,
            player, count, num_index, select_num, button_tag, card1_num, card2_num,
            resourceId, BackCardId, CorrectId, MistakeId;
    Handler handler;
    Runnable r;

    // view
    androidx.constraintlayout.widget.ConstraintLayout screen;
    TextView center_tv, header, p1_card, p2_card;
    ImageView result;

    // animator
    AnimatorSet FrontAnim, BackAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concentraion);
        // 変数セット
        TAG = "Concentration";
        NUM_PLAYER = 2;
        NUM_CARD = 20;
        POINT = 1;
        SLEEP_TIME = 1000; /* カードを裏返すまでの時間[ms] */
        FocusColor = Color.CYAN; /* 手番プレイヤー色 */
        NoneFocusColor = Color.TRANSPARENT; /* none手番プレイヤー色 */
        // viewをセット
        screen = findViewById(R.id.concentration_gameScreen);
        center_tv = findViewById(R.id.concentration_center_tv);
        header = findViewById(R.id.concentration_header);
        p1_card = findViewById(R.id.concentration_p1_card);
        p2_card = findViewById(R.id.concentration_p2_card);
        result = findViewById(R.id.concentration_result);

        // DrawableIDをセット
        BackCardId = getResources().getIdentifier("bicyclebackside", "drawable", getPackageName());
        CorrectId = getResources().getIdentifier("correct", "drawable", getPackageName());
        MistakeId = getResources().getIdentifier("mistake", "drawable", getPackageName());


        // 固定のリストを作成
        SuitList = new ArrayList<>(Arrays.asList("heart", "diamond", "clover", "spade")); /* ArraysAsListだと固定長になるため，追加や削除ができない */
        cardBackSide = new ArrayList<>(Arrays.asList("bicyclebackside")); /* 裏面カード表示用 */
        // buttonを読み込み＆リストに格納
        CardButtonList = new ArrayList<ImageButton>();
        for (int i=0; i < NUM_CARD; i++){
            int ButtonId = getResources().getIdentifier("concentration_button"+i, "id", getPackageName());
            CardButtonList.add(findViewById(ButtonId));
        }
        //初期化
        init();
    }

    // 戻る
    public void tapBack(View view) {
        Intent intent = new Intent(ConcentrationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 初期化
    public void init(){
        // 変数初期化
        player = 1;
        count = 0;
        // 得点をリセット
        PlayerPoint = new ArrayList<>();
        // 選択したカードのボタンリストを初期化
        selectedCards = new ArrayList<>();
        TempCardsButton = new ArrayList<>();
        // 残っているカードの番号リスト
        NumList = IntStream.range(0, 13).boxed().collect(Collectors.toList());
        //
        for (int i=0; i <NUM_PLAYER; i++) {
            PlayerPoint.add(0);
        }
        // 全てのCardのリストを初期化
        CardList = new ArrayList<>();
        for (int i=0; i < 13; i++){
            CardList.add(new ArrayList<String>(SuitList)); /* SuitListをコピーして代入 */
        }

        // viewをリセット
        resetViews();

        // 表示するcardを選択
        selectCards();

        // カードを表示する
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                center_tv.setVisibility(View.INVISIBLE);
                // player表示開始
                p1_card.setBackgroundColor(FocusColor);
                p2_card.setBackgroundColor(NoneFocusColor);
                p1_card.setVisibility(View.VISIBLE);
                p2_card.setVisibility(View.VISIBLE);
                // header表示
                header.setText("Player " + player);
                screen.setClickable(false);
                handler = new Handler();
                r = new Runnable() {
                    int counter = 0;

                    @Override
                    public void run() {
                        // UIスレッド
                        if (counter < CardButtonList.size()) {
                            showCards(CardButtonList.get(counter), BackCardId);
                            counter++;
                            handler.postDelayed(this, 50);
                        }else{
                            // ボタンを押下可能にする
                            turnButtons(true);
                        }
                    }
                };
                handler.post(r);
            }
        });
    }

    // viewのリセット
    public void resetViews(){
        // cardを非表示
        for (int i=0; i < NUM_CARD; i++){
            CardButtonList.get(i).setVisibility(View.INVISIBLE);
        }
        // screenをタップ可能にする
        screen.setClickable(true);
        // StartTextViewを表示
        center_tv.setText("Tap screen to start");
        center_tv.setVisibility(View.VISIBLE);

        // 題名を表示
        header.setText("神経衰弱");
        header.setVisibility(View.VISIBLE);

        // playerの得点をリセット＆非表示
        p1_card.setText("Player1:" + 0);
        p2_card.setText("Player2:" + 0);
        p1_card.setVisibility(View.INVISIBLE);
        p2_card.setVisibility(View.INVISIBLE);

        // resultを非表示
        result.setVisibility(View.INVISIBLE);
    }

    // 全てのボタンを有効/無効
    public void turnButtons(boolean bool){
        for (int i=0; i < CardButtonList.size(); i++){
            CardButtonList.get(i).setClickable(bool);
        }
    }

    // cardを20枚選ぶ
    public void selectCards(){
        // 選択するカードリストを初期化する
        selectCardList = new ArrayList<>();
        // 10組選ぶ
        for (int i=0; i < NUM_CARD/2; i++){
            num_index = (int) (Math.random() * (NumList.size()));
            select_num = NumList.get(num_index);
            Collections.shuffle(CardList.get(select_num)); /* suitをシャッフル */
            sbList = CardList.get(select_num).subList(0, 2);
            for (int j=0; j < 2; j++){
                TempCard = new ArrayList<String>(Arrays.asList(sbList.get(j), String.valueOf(select_num+1)));
                selectCardList.add(TempCard);
            }
            // 選んだスートを削除
            CardList.get(select_num).removeAll(sbList);
            // もしカードを4枚選択してしまったら，選択候補から削除
            if(CardList.get(select_num).size() == 0){
                NumList.remove(NumList.indexOf(select_num));
            }
        }
        Collections.shuffle(selectCardList);
    }

    // 第一引数のカードを第二引数の画像idで表示する
    public void showCards(ImageButton imageButton, int id){
        imageButton.setBackgroundResource(id);
        imageButton.setVisibility(View.VISIBLE);
    }

    // cardを返すアニメーションを実行
    public void flipCard(ImageButton imageButton, int id, AnimatorSet front, AnimatorSet back){
        // buttonを無効化
        turnButtons(false);
        front.setTarget(imageButton);
        front.start();

        // FrontAnimationが終了したら実行する内容
        front.addListener(new AnimatorListenerAdapter() {
           public void onAnimationEnd(Animator animation) {
               imageButton.setBackgroundResource(id);
               back.setTarget(imageButton);
               back.start();
           }
        });
    }

    // cardをタップしたときの挙動
    public void tapCard(View view){
        button_tag = Integer.parseInt(view.getTag().toString()); /* tagを取得 */

        TempCard = selectCardList.get(button_tag); /* 選んだカードの情報 */
        selectedCards.add(TempCard); /* 選んだカードの情報を記録 */

        TempCardsButton.add((ImageButton) view); /* 選んだカードのボタンを記憶 */

        String suit = TempCard.get(0); /* 選んだカードのスート*/
        String num = TempCard.get(1); /* 選んだカードの数字*/
        resourceId = getResources().getIdentifier(suit + num, "drawable", getPackageName()); /* カードを表示するためのIDを取得 */

        // animatorをセット
        FrontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.front_anim);
        BackAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.back_anim);
        flipCard((ImageButton) view, resourceId, FrontAnim, BackAnim); /* カードを表示 */

        // BackAnimationが終了したら実行する内容
        BackAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                turnButtons(true);
                // ボタンを無効化
                view.setClickable(false);
                if (TempCardsButton.size() == 2) { /* 2回目の選択なら */
                    doJudgement();
                }
            }
        });
    }

    // 二枚目を引いた後にジャッジを実行する
    public void doJudgement(){
        // ボタンを無効化
        turnButtons(false);
        // 正解チェックを実行
        boolean bool = checkCards();
        if(bool){ /* 正解なら */
            result.setImageResource(CorrectId);
            // ポイントを加算
            addPoint(player, POINT);
            count++;
            // 終了判定
            if(count == NUM_CARD/2){
                // 結果発表
                makeResult();
                return;
            }
        }else {
            result.setImageResource(MistakeId);
        }
        // 結果を表示
        result.setVisibility(View.VISIBLE);

        // UI変更のために別スレッドを立てる
        new Thread(new Runnable(){
            public void run(){
                // Handlerを使用してメイン(UI)スレッドに処理を依頼する
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.setVisibility(View.INVISIBLE);
                        if(bool){ /* 正解なら */
                            TempCardsButton.forEach(item -> item.setVisibility(View.INVISIBLE));
                        }else{ /* 不正解なら */
                            playerChange(player);
                        }
                        // カードを裏返す(正解の場合も，下に記述のボタン有効化条件にアニメーションの終了をセットしているので仮想的にanimationを実行する)
                        for(int i = 0; i < TempCardsButton.size(); i++) {
                            sleep(SLEEP_TIME);
                            // animatorをセット(敢えて引数で渡すことで同じアニメーションでもimageButtonごとに異なるアニメーションであることを明示している)
                            FrontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.front_anim);
                            BackAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.back_anim);
                            flipCard(TempCardsButton.get(i), BackCardId, FrontAnim, BackAnim); /* 裏返す */
                        }
                        // Animationが終了したらボタンを有効化
                        BackAnim.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                // ボタンを有効化
                                turnButtons(true);
                            }
                        });
                        // 選択したカード,ボタンリストをリセット
                        TempCardsButton.clear();
                        selectedCards.clear();
                    }
                });
            }
        }).start();
    }

    // 正解チェック
    public boolean checkCards(){
        // 正解判定値を初期化
        boolean bool = false;
        // チェックを実行
        card1_num = Integer.parseInt(selectedCards.get(0).get(1));
        card2_num = Integer.parseInt(selectedCards.get(1).get(1));
        if(card1_num == card2_num){
            bool = true;
        }
        return bool;
    }

    // 正解時のプレイヤーへの加算
    public void addPoint(int player_num, int point){
        /*引数１：加算するプレイヤー文字列
        * 引数２：加算するポイント数*/
        // データ上でポイントを加算
        PlayerPoint.set(player_num-1, PlayerPoint.get(player_num-1) + point);
        // view上でポイントを加算
        p1_card.setText("Player1:" + PlayerPoint.get(0));
        p2_card.setText("Player2:" + PlayerPoint.get(1));
    }

    public void playerChange(int player_num){
        if(player_num ==1){
            player = 2;
            p1_card.setBackgroundColor(NoneFocusColor);
            p2_card.setBackgroundColor(FocusColor);
        }else{
            player = 1;
            p1_card.setBackgroundColor(FocusColor);
            p2_card.setBackgroundColor(NoneFocusColor);
        }
        header.setText("Player " + player);
    }

    public void makeResult(){
        // 表示をリセット
        resetViews();
        // 勝敗
        if (PlayerPoint.get(0) == PlayerPoint.get(1)){
            ResultStr = "Draw (∩´﹏`∩)" + "\nPoint(2 Players): " + PlayerPoint.get(player - 1) + "\n\nTap screen to continue";
        }else{
            // リスト内の最大値を求める
            int max = Collections.max(PlayerPoint);
            // 最大値を与えているもののインデックスを求める
            int maxIndex = PlayerPoint.indexOf(max);
            player = maxIndex + 1;
            ResultStr = "Winner: Player" + player + "٩(ˊᗜˋ*)و\nPoint: " + PlayerPoint.get(player - 1) + "\n\nTap screen to continue";
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

    // リソースネーム(スート+数字)をスートの文字列と数字に分割
    public List<String> divideStr(String str){
        List<String> divide = new ArrayList<String>();
        String intStr = str.replaceAll("[^0-9]", "");
        divide.add(intStr);
        String suitStr = str.replaceAll("intStr", "");
        divide.add(suitStr);
        return divide;
    }

    // 待機
    public void sleep (int sleep_time){
        try {
            Thread.sleep(sleep_time); //Sleepする
        } catch (InterruptedException e) {
            Log.d(TAG, "Interrupted");
        }
    }
}
