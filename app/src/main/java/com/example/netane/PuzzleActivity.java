package com.example.netane;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PuzzleActivity extends AppCompatActivity {

    TextView clearTime, clearText;
    Handler handler;
    Runnable runnable;
    long startTime, elapsedTime;
    int shuffleTimes;
    int rowSize, colSize, tmp, n, m;
    LinearLayout gameScreen;
    TextView[] box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        clearText = findViewById(R.id.text_clear);
        clearTime = findViewById(R.id.text_clearTime);
        gameScreen = findViewById(R.id.linearlayout_gameScreen);
        rowSize = getIntent().getIntExtra("row", 0);
        colSize = getIntent().getIntExtra("col", 0);
        gameScreen.setWeightSum(rowSize);
        box = new TextView[rowSize * colSize];
        tmp = 0;
        shuffleTimes = rowSize * colSize * 2;

//        パズルを生成
        for (int i = 0; i < rowSize; i++) {
            LinearLayout linearLayout = new LinearLayout(PuzzleActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(colSize);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            for (int j = 0; j < colSize; j++) {
                box[tmp] = new TextView(PuzzleActivity.this);
                box[tmp].setText(String.valueOf(tmp + 1));
                box[tmp].setTextSize(100 / colSize);
                box[tmp].setGravity(Gravity.CENTER);
                box[tmp].setClickable(true);
                box[tmp].setOnClickListener(new View.OnClickListener() {
                    int i = tmp;

                    @Override
                    public void onClick(View v) {
                        checkMove(i);
                    }
                });
                box[tmp].setBackgroundResource(R.drawable.border);
                box[tmp].setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                if (tmp + 1 == colSize * rowSize) {
                    box[tmp].setText("");
                }
                linearLayout.addView(box[tmp]);
                tmp++;
            }
            gameScreen.addView(linearLayout);
        }
    }

    public void tapBack(View view) {
        Intent intent = new Intent(PuzzleActivity.this, PuzzleSettingActivity.class);
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
//        タイマーストップ
        handler.removeCallbacks(runnable);
    }

    public void tapRestart(View view) {
        onTimer();
        shuffle();
        clearText.setText("");
        for (int i = 0; i < box.length; i++) {
            box[i].setEnabled(true);
        }
    }

    public void move(TextView text1, TextView text2) {
        text2.setText(text1.getText().toString());
        text1.setText("");
    }

    public void checkMove(int i) {
//        数字の移動処理
        if ((i % colSize != 0) && (box[i - 1].getText().toString().length() == 0)) {
            move(box[i], box[i - 1]);
        } else if ((i % colSize != colSize - 1) && (box[i + 1].getText().toString().length() == 0)) {
            move(box[i], box[i + 1]);
        } else if ((i >= colSize) && (box[i - colSize].getText().toString().length() == 0)) {
            move(box[i], box[i - colSize]);
        } else if ((i < (colSize * rowSize - colSize)) && (box[i + colSize].getText().toString().length() == 0)) {
            move(box[i], box[i + colSize]);
        }
        if (clearCheck()) {
            clearText.setText("CLEAR!!");
            stopTimer();
            for (int j = 0; j < box.length; j++) {
                box[j].setEnabled(false);
            }
        }
    }

    public void shuffle() {
//        数字をシャッフル
        String tmp;
        for (int i = 0; i < shuffleTimes; i++) {
            n = (int) Math.floor(Math.random() * (box.length - 1));
            m = (int) Math.floor(Math.random() * (box.length - 1));
            if (n != m) {
                tmp = box[n].getText().toString();
                box[n].setText(box[m].getText().toString());
                box[m].setText(tmp);
            } else {
                i--;
            }
        }
    }

    public boolean clearCheck() {
        for (int i = 0; i < box.length - 1; i++) {
            if (box[i].getText().toString() != String.valueOf(i + 1)) {
                return false;
            }
        }
        return true;
    }
}
