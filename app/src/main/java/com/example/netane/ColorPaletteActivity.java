package com.example.netane;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ColorPaletteActivity extends AppCompatActivity implements View.OnClickListener {
    TextView clearTime, clearText, paletteCount;
    Handler handler, hdl;
    Runnable runnable, rnb;
    long startTime, elapsedTime;
    int num_color, r, g, b, PermissibleError, randomColor, answerColor, paletteColor, index, temp, counter, matchRate;
    View goal, palette;
    Button buttonNext, buttonReset;
    ArrayList<Integer> colorArray;
    List<Button> ButtonList;
    LinearLayout gameScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpalette);
        ButtonList = new ArrayList<>();
        // 初期化
        init();
        newGame();
    }

    //    初期化
    public  void init(){
        PermissibleError = 10;
        clearText = findViewById(R.id.text_clear);
        clearTime = findViewById(R.id.text_clearTime);
        gameScreen = findViewById(R.id.linearlayout_gameScreen);
        num_color = getIntent().getIntExtra("num_color", 0);
        paletteCount = findViewById(R.id.palette_count);

        for (int i=0; i < 6; i++){
            int ButtonId = getResources().getIdentifier("circle"+(i+1), "id", getPackageName());
            ButtonList.add(findViewById(ButtonId));
            ButtonList.get(i).setOnClickListener(this);
        }

        buttonNext = findViewById(R.id.next);
        buttonReset = findViewById(R.id.reStart);
        palette = findViewById(R.id.palette_color);
        goal = findViewById(R.id.goal_color);
    }

    //    newGame
    public void newGame(){
        counter = 0; // いくつ色を足したか
        clearText.setText("");
        paletteCount.setText(String.format("残り%d色", num_color)); // 残り何色か
        buttonClickable(true);
        // リセットボタンを有効
        buttonReset.setClickable(true);
        // Nextボタンを無効
        buttonNext.setText("");
        buttonNext.setClickable(false);
        // 色を7色作成
        selectColor();
        // 正解の色を設定
        setAnswerColor();
        // 各ボタンに色を設定
        setColor();
        // タイマースタート
        onTimer();
    }

    public void tapBack(View view) {
        Intent intent = new Intent(ColorPaletteActivity.this, ColorPaletteSettingActivity.class);
        startActivity(intent);
        finish();
    }

    public void onTimer() {
//        タイマー起動
        handler = new Handler();
        startTime = System.currentTimeMillis();
        runnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS", Locale.US);
                clearTime.setText(sdf.format(elapsedTime));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 10);
            }
        };
        handler.postDelayed(runnable, 10);
    }

    public void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    public void tapRestart(View view) {
        if(hdl!=null){
            hdl.removeCallbacks(rnb);
        }
        clearText.setText("");
        counter = 0; // いくつ色を足したか
        paletteCount.setText(String.format("残り%d色", num_color)); // 残り何色か
        paletteColor = colorArray.get(0);
        palette.setBackgroundColor(colorArray.get(0));
        buttonClickable(true);
        // Nextボタンを無効
        buttonNext.setText("");
        buttonNext.setClickable(false);
    }

    public void tapNext(View view){
        hdl.removeCallbacks(rnb);
        newGame();
    }

    // ボタンの有効無効切り替え
    public void buttonClickable(boolean bool){
        for (int i=0; i < 6; i++){
            ButtonList.get(i).setEnabled(bool);
        }
    }

    //    ボタンクリック時の動作
    public void onClick(View view) {
        counter++;
        index = Integer.parseInt((String) view.getTag());
        ButtonList.get(index-1).setEnabled(false);

        // パレットに選択した色を混ぜる
        paletteColor = mixColor(paletteColor, colorArray.get(index), counter);
        palette.setBackgroundColor(paletteColor);
        //  クリアチェック
        if (counter == num_color) {
            buttonClickable(false);
            if (clearCheck()) {
                stopTimer();
                clearText.setText(String.format("Clear! %d%% Match", matchRate));
                // Nextボタンを有効
                buttonNext.setText("Next");
                buttonNext.setClickable(true);
                // リセットボタンを無効
                buttonReset.setClickable(false);
                // トーストの表示
                Context context = getApplicationContext();
                CharSequence text = "Tap \"Next\" or Wait 2s";
                int duration = Toast.LENGTH_SHORT; // 約２秒 longなら約４秒
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                // 処理待ち
                hdl = new Handler();
                rnb = new Runnable() {
                    public void run() {
                        newGame();
                    }
                };
                hdl.postDelayed(rnb, 2000);
            } else {
                clearText.setText(String.format("Incorrect! %d%% Match", matchRate));

                // 処理待ち
                hdl = new Handler();
                rnb = new Runnable() {
                    public void run() {
                        buttonReset.performClick();
                    } // リセットボタンを押したことにする
                };
                hdl.postDelayed(rnb, 1500);
            }
        }
        // 残り混色回数を設定
        paletteCount.setText(String.format("残り%d色", num_color - counter)); // 残り何色か
    }

    //    色を選択
    public void selectColor() {
        colorArray = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Random rand = new Random();
            do {
                r = rand.nextInt(255);
                g = rand.nextInt(255);
                b = rand.nextInt(255);
            } while ((Math.abs(r - g) < PermissibleError && Math.abs(g - b) < PermissibleError && Math.abs(b - r) < PermissibleError));
            randomColor = Color.rgb(r, g, b);
            colorArray.add(randomColor);
        }
    }

    //    正解の色を作成
    public void setAnswerColor() {
        Collections.shuffle(colorArray);
        answerColor = colorArray.get(0);
        for (int i = 1; i <= num_color; i++) {
            answerColor = mixColor(answerColor, colorArray.get(i), i);
        }
    }

    //    色を調合
    public int mixColor(int originColor, int addColor, int count) {
        int originRed, originGreen, originBlue, addRed, addGreen, addBlue;
        originRed = Color.red(originColor);
        originGreen = Color.green(originColor);
        originBlue = Color.blue(originColor);
        addRed = Color.red(addColor);
        addGreen = Color.green(addColor);
        addBlue = Color.blue(addColor);

        // 各色毎に平均を算出
        originRed = (originRed * count + addRed) / (count + 1) ;
        originGreen = (originGreen * count + addGreen) / (count + 1);
        originBlue = (originBlue * count + addBlue) / (count + 1);
        return Color.rgb(originRed, originGreen, originBlue);
    }

    //    色を設定
    public void setColor() {
        temp = colorArray.get(0);
        colorArray.remove(0);
        Collections.shuffle(colorArray);
        colorArray.add(0, temp);
        // 最初の色を設定
        paletteColor = colorArray.get(0);
        // 各ボタンに色をセット
        goal.setBackgroundColor(answerColor);
        palette.setBackgroundColor(colorArray.get(0));

        // setButtonColor
        for (int i=0; i < 6; i++){
//            ButtonList.get(i).setBackgroundColor(colorArray.get(i+1));
            customView(ButtonList.get(i), colorArray.get(i+1));
        }
    }

    //    類似度を計算
    public int calMatch() {
        int answerRed, answerGreen, answerBlue, paletteRed, paletteGreen, paletteBlue, result;
        double dstRed, dstGreen, dstBlue;
        final int MAX = 255 * 3;

        answerRed = Color.red(answerColor);
        answerGreen = Color.green(answerColor);
        answerBlue = Color.blue(answerColor);
        paletteRed = Color.red(paletteColor);
        paletteGreen = Color.green(paletteColor);
        paletteBlue = Color.blue(paletteColor);
        // マッチ度計算(色差)
        dstRed = Math.abs(answerRed - paletteRed);
        dstGreen = Math.abs(answerGreen - paletteGreen);
        dstBlue = Math.abs(answerBlue - paletteBlue);
//        100%－誤差
        result = (int) ((1 - (dstRed + dstGreen + dstBlue) / MAX) * 100);
        return result;
    }

    //    クリアチェック
    public boolean clearCheck() {
        matchRate = calMatch();
        return paletteColor == answerColor || matchRate >= 99;
    }

    public static void customView(View v, int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(backgroundColor);
        shape.setStroke(3, Color.BLACK);
        v.setBackground(shape);
    }
}