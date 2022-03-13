package com.example.netane;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.Locale;
import java.util.Random;

public class ColorPaletteActivity extends AppCompatActivity implements View.OnClickListener {
    TextView clearTime, clearText, paletteCount;
    Handler handler, hdl;
    Runnable runnable, rnb;
    long startTime, elapsedTime;
    int num_color, r, g, b, randomColor, answerColor, paletteColor, index, temp, counter, matchRate;
    View goal, palette;
    Button button1, button2, button3, button4, button5,
            button6, button7, button8, button9, button10,
            buttonNext, buttonReset;
    ArrayList<Integer> colorArray;
    LinearLayout gameScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpalette);
        init();
        newGame();
    }

    //    初期化
    public  void init(){
        clearText = findViewById(R.id.text_clear);
        clearTime = findViewById(R.id.text_clearTime);
        gameScreen = findViewById(R.id.linearlayout_gameScreen);
        num_color = getIntent().getIntExtra("num_color", 0);
        paletteCount = findViewById(R.id.palette_count);

        button1 = findViewById(R.id.circle1);
        button1.setOnClickListener(this);
        button2 = findViewById(R.id.circle2);
        button2.setOnClickListener(this);
        button3 = findViewById(R.id.circle3);
        button3.setOnClickListener(this);
        button4 = findViewById(R.id.circle4);
        button4.setOnClickListener(this);
        button5 = findViewById(R.id.circle5);
        button5.setOnClickListener(this);
        button6 = findViewById(R.id.circle6);
        button6.setOnClickListener(this);
        button7 = findViewById(R.id.circle7);
        button7.setOnClickListener(this);
        button8 = findViewById(R.id.circle8);
        button8.setOnClickListener(this);
        button9 = findViewById(R.id.circle9);
        button9.setOnClickListener(this);
        button10 = findViewById(R.id.circle10);
        button10.setOnClickListener(this);
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
        // 色を11色作成
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
        onTimer();
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
        button1.setEnabled(bool);
        button2.setEnabled(bool);
        button3.setEnabled(bool);
        button4.setEnabled(bool);
        button5.setEnabled(bool);
        button6.setEnabled(bool);
        button7.setEnabled(bool);
        button8.setEnabled(bool);
        button9.setEnabled(bool);
        button10.setEnabled(bool);
    }

    //    ボタンクリック時の動作
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circle1:
                button1.setEnabled(false);
                index = 1;
                break;
            case R.id.circle2:
                button2.setEnabled(false);
                index = 2;
                break;
            case R.id.circle3:
                button3.setEnabled(false);
                index = 3;
                break;
            case R.id.circle4:
                button4.setEnabled(false);
                index = 4;
                break;
            case R.id.circle5:
                button5.setEnabled(false);
                index = 5;
                break;
            case R.id.circle6:
                button6.setEnabled(false);
                index = 6;
                break;
            case R.id.circle7:
                button7.setEnabled(false);
                index = 7;
                break;
            case R.id.circle8:
                button8.setEnabled(false);
                index = 8;
                break;
            case R.id.circle9:
                button9.setEnabled(false);
                index = 9;
                break;
            case R.id.circle10:
                button10.setEnabled(false);
                index = 10;
                break;
        }
//        パレットに選択した色を混ぜる
        paletteColor = mixColor(paletteColor, colorArray.get(index));
        palette.setBackgroundColor(paletteColor);
        counter++;
        //  クリアチェック
        if (counter == num_color) {
            buttonClickable(false);
            if (clearCheck()) {
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
//                // トーストの表示
//                Context context = getApplicationContext();
//                CharSequence text = "Tap \"PALETTE RESET\" or Auto Reset in 3s";
//                int duration = Toast.LENGTH_SHORT; // 約２秒 longなら約４秒
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

                // 処理待ち
                hdl = new Handler();
                rnb = new Runnable() {
                    public void run() {
                        buttonReset.performClick();
                    } // リセットボタンを押したことにする
                };
                hdl.postDelayed(rnb, 1500);
            }
            stopTimer();
        }
        // 残り混色回数を設定
        paletteCount.setText(String.format("残り%d色", num_color - counter)); // 残り何色か
    }

    //    色を選択
    public void selectColor() {
        colorArray = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Random rand = new Random();
            do {
                r = rand.nextInt(255);
                g = rand.nextInt(255);
                b = rand.nextInt(255);
            } while ((Math.abs(r - g) < 20 && Math.abs(g - b) < 20 && Math.abs(b - r) < 20));
            randomColor = Color.rgb(r, g, b);
            colorArray.add(randomColor);
        }
    }

    //    正解の色を作成
    public void setAnswerColor() {
        Collections.shuffle(colorArray);
        answerColor = colorArray.get(0);
        for (int i = 1; i <= num_color; i++) {
            answerColor = mixColor(answerColor, colorArray.get(i));
        }
    }

    //    色を調合
    public int mixColor(int originColor, int addColor) {
        int originRed, originGreen, originBlue, addRed, addGreen, addBlue;
        originRed = Color.red(originColor);
        originGreen = Color.green(originColor);
        originBlue = Color.blue(originColor);
        addRed = Color.red(addColor);
        addGreen = Color.green(addColor);
        addBlue = Color.blue(addColor);

        // 各色毎に平均を算出
        originRed = (originRed + addRed) / 2;
        originGreen = (originGreen + addGreen) / 2;
        originBlue = (originBlue + addBlue) / 2;
        int mixedColor = Color.rgb(originRed, originGreen, originBlue);
        return mixedColor;
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
        button1.setBackgroundColor(colorArray.get(1));
        button2.setBackgroundColor(colorArray.get(2));
        button3.setBackgroundColor(colorArray.get(3));
        button4.setBackgroundColor(colorArray.get(4));
        button5.setBackgroundColor(colorArray.get(5));
        button6.setBackgroundColor(colorArray.get(6));
        button7.setBackgroundColor(colorArray.get(7));
        button8.setBackgroundColor(colorArray.get(8));
        button9.setBackgroundColor(colorArray.get(9));
        button10.setBackgroundColor(colorArray.get(10));
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
        if (paletteColor == answerColor || matchRate >= 99) {
            return true;
        } else {
            return false;
        }
    }
}