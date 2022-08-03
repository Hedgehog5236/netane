package com.example.netane;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HitBlowSettingActivity extends AppCompatActivity {
    // 変数
    List<Button> ButtonList;
    Button Three, Four, Limit30, Limit60, Limit120,
            DefaultSizeButton, DefaultTimeLimitButton;
    int Size, TimeLimit, DefaultSize, DefaultTimeLimit, DefaultSizeId, DefaultTimeLimitId,
            FocusColor, NoneFocusColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitblow_setting);
        setVariables();
        setViews();
    }

    public void setVariables(){
        // 変数セット
        DefaultSize = 4;
        DefaultTimeLimit = 60;
        Size = DefaultSize;
        TimeLimit = DefaultTimeLimit;
        // Colorセット
        FocusColor = Color.WHITE;
        NoneFocusColor = Color.GRAY;
        // defaultセット
        DefaultSizeId = getResources().getIdentifier("HitBlow"+DefaultSize, "id", getPackageName());
        DefaultTimeLimitId = getResources().getIdentifier("HitBlow_limit"+DefaultTimeLimit, "id", getPackageName());
        DefaultSizeButton = findViewById(DefaultSizeId);
        DefaultTimeLimitButton = findViewById(DefaultTimeLimitId);
    }

    public void setViews(){
        Three = findViewById(R.id.HitBlow3);
        Four = findViewById(R.id.HitBlow4);
        Limit30 = findViewById(R.id.HitBlow_limit30);
        Limit60 = findViewById(R.id.HitBlow_limit60);
        Limit120 = findViewById(R.id.HitBlow_limit120);

        // list初期化
        ButtonList = new ArrayList<>(Arrays.asList(Three, Four, Limit30, Limit60, Limit120));
        for (Button btn : ButtonList){
            turnFocus(btn, false);
        }
        turnFocus(DefaultSizeButton, true);
        turnFocus(DefaultTimeLimitButton, true);
    }

    public void tapSize(View view){
        turnFocus(view, true);
        if(view.equals(Three)){
            turnFocus(Four, false);
            Size = 3;
        }else if(view.equals(Four)){
            turnFocus(Three, false);
            Size = 4;
        }
    }

    public void tapLimit(View view){
        turnFocus(view, true);
        if(view.equals(Limit30)){
            turnFocus(Limit60, false);
            turnFocus(Limit120, false);
            TimeLimit = 30;
        }else if(view.equals(Limit60)){
            turnFocus(Limit30, false);
            turnFocus(Limit120, false);
            TimeLimit = 60;
        }else if(view.equals(Limit120)){
            turnFocus(Limit30, false);
            turnFocus(Limit60, false);
            TimeLimit = 120;
        }
    }


    public void turnFocus(View button, boolean bool){
        if(bool){
            button.setBackgroundColor(FocusColor);
            button.setClickable(false);
        }else{
            button.setBackgroundColor(NoneFocusColor);
            button.setClickable(true);
        }
    }

    public void tapEnter(View view){
        Intent intent = new Intent(this, HitBlowActivity.class);
        intent.putExtra("Size", Size);
        intent.putExtra("TimeLimit", TimeLimit);
        startActivity(intent);
    }

    public void tapBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}