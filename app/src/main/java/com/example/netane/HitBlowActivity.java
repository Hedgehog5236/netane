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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HitBlowActivity extends AppCompatActivity {
    // 変数
    List< List<CustomListItem> > HistoryList;
    List< List<Integer> > PlayersAnswerNumber;
    List<Integer> PlayersInputNumber;
    List<String> HitBlowResult;
    CustomListAdapter adapter;
    int Size, TimeLimit, TimeRemaining, Player,
            FocusColor, NoneFocusColor, ClickableColor, UnClickableColor;
    long startTime, elapsedTime;
    boolean InitKey, CorrectKey;
    Handler TimerHandler;
    Runnable TimerRunnable;

    // Views
    TextView ElapsedTimeTv, CenterTv, NotifyTv, Restart,
            P1Tv, P2Tv;
    Button Info, Enter;
    List<ListView> Histories;
    LinearLayout InputNumView, InputNumScreen, AnswerP1, AnswerP2;
    CircularProgressIndicator cpi;
    List< List<TextView> > AnswerNumbersTv;
    List<TextView> InputNumbersTv;
    List<Button> AllNumButtons;

    //Animator
    AnimatorSet FadeInOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitblow);
        setVariables();
        findViews();
        init();
    }

    public void init(){
        // 変数初期化
        Player = 1;
        InitKey = true;
        CorrectKey = false;
        PlayersInputNumber = new ArrayList<>();
        PlayersAnswerNumber = new ArrayList<>();
        HistoryList = new ArrayList<>(Arrays.asList(new ArrayList<>(), new ArrayList<>()));
        // 出力結果を初期化
        for (int i=0; i<2; i++){
            adapter = new CustomListAdapter(this, R.layout.listview_row, HistoryList.get(i));
            Histories.get(i).setAdapter(adapter);
        }
        // viewをセット
        resetViews(false);
        // 入力開始
        ReadyToInput(1);
    }

    public void setVariables(){
        // intentから変数を受け取る
        Size = getIntent().getIntExtra("Size", 0);
        TimeLimit = getIntent().getIntExtra("TimeLimit", 0);
        // Colorセット
        FocusColor = Color.CYAN;
        NoneFocusColor = Color.TRANSPARENT;;
        ClickableColor = Color.WHITE;
        UnClickableColor = Color.GRAY;
        //viewsList
        InputNumbersTv = new ArrayList<>();
        AnswerNumbersTv = new ArrayList<>(Arrays.asList(new ArrayList<>(), new ArrayList<>()));
        AllNumButtons = new ArrayList<>();
    }

    public void findViews(){
        AnswerP1 = findViewById(R.id.HitBlow_P1_num);
        AnswerP2 = findViewById(R.id.HitBlow_P2_num);
        Histories = new ArrayList<>(Arrays.asList(findViewById(R.id.HitBlow_p1_history), findViewById(R.id.HitBlow_p2_history)));
        CenterTv = findViewById(R.id.HitBlow_center_tv);
        NotifyTv = findViewById(R.id.HitBlow_notifyTv);
        P1Tv = findViewById(R.id.HitBlow_P1);
        P2Tv = findViewById(R.id.HitBlow_P2);
        Info = findViewById(R.id.HitBlow_info);
        InputNumView = findViewById(R.id.HitBlow_input_num);
        InputNumScreen = findViewById(R.id.HitBlow_input_screen);
        cpi = findViewById(R.id.HitBlow_cpi);
        ElapsedTimeTv = findViewById(R.id.HitBlow_elapsedTime);
        Enter = findViewById(R.id.HitBlow_enter);
        Restart = findViewById(R.id.HitBlow_restart);
        for (int i=0; i<10; i++){
            int TempButtonId = getResources().getIdentifier("HitBlow_Button"+(i), "id", getPackageName());
            AllNumButtons.add(findViewById(TempButtonId));
        }
        for (int i=0; i < 4; i++){
            int TempInputTvId = getResources().getIdentifier("HitBlow_input"+(i+1), "id", getPackageName());
            InputNumbersTv.add(findViewById(TempInputTvId));
            for (int j=0; j < 2; j++){
                int TempAnswerTvId = getResources().getIdentifier("HitBlow_Answer"+(j+1)+"_"+(i+1), "id", getPackageName());
                AnswerNumbersTv.get(j).add(findViewById(TempAnswerTvId));
            }
        }

        // CircularProgressIndicator
        cpi.setIndeterminate(false);
        cpi.setMax(TimeLimit);

        // Size=3のときviewを減らす
        if (Size==3){
            InputNumbersTv.get(3).setVisibility(View.GONE);
            AnswerNumbersTv.get(0).get(3).setVisibility(View.GONE);
            AnswerNumbersTv.get(1).get(3).setVisibility(View.GONE);
        }
    }

    public void resetViews(boolean bool){
        if (bool){
            P1Tv.setVisibility(View.VISIBLE);
            P2Tv.setVisibility(View.VISIBLE);
            Info.setVisibility(View.VISIBLE);
            Histories.get(0).setVisibility(View.VISIBLE);
            Histories.get(1).setVisibility(View.VISIBLE);
            InputNumView.setVisibility(View.VISIBLE);
            InputNumScreen.setVisibility(View.VISIBLE);
            cpi.setVisibility(View.VISIBLE);
            ElapsedTimeTv.setVisibility(View.VISIBLE);
            AnswerP1.setVisibility(View.VISIBLE);
            AnswerP2.setVisibility(View.VISIBLE);
            // centerTvだけ非表示
            CenterTv.setVisibility(View.INVISIBLE);
        }else{
            P1Tv.setVisibility(View.INVISIBLE);
            P2Tv.setVisibility(View.INVISIBLE);
            Info.setVisibility(View.INVISIBLE);
            Histories.get(0).setVisibility(View.INVISIBLE);
            Histories.get(1).setVisibility(View.INVISIBLE);
            InputNumView.setVisibility(View.INVISIBLE);
            InputNumScreen.setVisibility(View.INVISIBLE);
            cpi.setVisibility(View.INVISIBLE);
            ElapsedTimeTv.setVisibility(View.INVISIBLE);
            NotifyTv.setVisibility(View.INVISIBLE);
            AnswerP1.setVisibility(View.INVISIBLE);
            AnswerP2.setVisibility(View.INVISIBLE);
            Restart.setVisibility(View.INVISIBLE);
            // centerTvだけ表示
            CenterTv.setVisibility(View.VISIBLE);
        }
        for (int i=0; i<Size; i++){
            for (int j=0; j<2; j++){
                AnswerNumbersTv.get(j).get(i).setText("?");
            }
        }
        Enter.setClickable(false);
        Enter.setBackgroundColor(UnClickableColor);

    }

    public void ReadyToInput(int player){
        onTimer();
        // InputViewsを初期化
        initInputViews();
        CenterTv.setText("Player " + player + "\nInput your numbers");
        InputNumView.setVisibility(View.VISIBLE);
        InputNumScreen.setVisibility(View.VISIBLE);

    }

    public void initInputViews(){
        // InputNumView, Enterを初期化
        for (int i=0; i<10; i++){
            AllNumButtons.get(i).setClickable(true);
            AllNumButtons.get(i).setBackgroundColor(ClickableColor);
        }
        Enter.setClickable(false);
        Enter.setBackgroundColor(UnClickableColor);

        // InputNumScreenを初期化
        for (int i=0; i < Size; i++){
            InputNumbersTv.get(i).setText("-");
        }
    }

    public void changePlayer(int player){
        // Playerをチェンジ
        if(player == 1){
            Player = 2;
            P1Tv.setBackgroundColor(NoneFocusColor);
            P2Tv.setBackgroundColor(FocusColor);
        }else{
            Player = 1;
            P1Tv.setBackgroundColor(FocusColor);
            P2Tv.setBackgroundColor(NoneFocusColor);
        }
        // InputViewsを初期化
        initInputViews();

        // Playerが入力した値リストをクリア
        PlayersInputNumber.clear();
        //animation
        if (PlayersAnswerNumber.size() >= 2){
            animeFade("Player "+Player);
        }
        //stopTimer
        stopTimer();
        //startTimer
        onTimer();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void tapRecall(View view){
        int tempPlayer = Integer.parseInt((String) view.getTag());

        View layout = getLayoutInflater().inflate( R.layout.custom_alert_dialog1, null );

        AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .create();

        TextView tvTitle = layout.findViewById(R.id.custom_alert_title);
        tvTitle.setTextSize(18);
        tvTitle.setText("Player " + tempPlayer + "'s Number will be displayed.");

        TextView tvMessage = layout.findViewById(R.id.custom_alert_message);
        tvMessage.setTextSize(20);
        tvMessage.setText("Are you sure to display it?");

        Button btnPositive = layout.findViewById(R.id.custom_alert_positive_button);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // クリックしたときの処理
                mDialog.dismiss();
                makeRecall(tempPlayer);
            }
        });
        Button btnNegative = layout.findViewById(R.id.custom_alert_negative_button);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // クリックしたときの処理
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void tapBackSpace(View view){
        int len = PlayersInputNumber.size();
        if(len != 0){
            PlayersInputNumber.remove(len-1);
            InputNumbersTv.get(len-1).setText("-");
            Enter.setClickable(false);
            Enter.setBackgroundColor(UnClickableColor);
            for (int i=0; i<10; i++){
                if(!(PlayersInputNumber.contains(i))){
                    AllNumButtons.get(i).setClickable(true);
                    AllNumButtons.get(i).setBackgroundColor(ClickableColor);
                }
            }
        }
    }

    public void tapNum(View view){
        int num = Integer.parseInt((String) view.getTag());
        PlayersInputNumber.add(num);
        InputNumbersTv.get(PlayersInputNumber.size()-1).setText((CharSequence) view.getTag());
        AllNumButtons.get(num).setClickable(false);
        AllNumButtons.get(num).setBackgroundColor(UnClickableColor);
        if(PlayersInputNumber.size() == Size){
            for (int i=0; i<10; i++){
                AllNumButtons.get(i).setClickable(false);
                AllNumButtons.get(i).setBackgroundColor(UnClickableColor);
            }
            Enter.setClickable(true);
            Enter.setBackgroundColor(ClickableColor);
        }
    }

    public void tapEnter(View view){
        if(PlayersAnswerNumber.size() == 0) {
            PlayersAnswerNumber.add(new ArrayList<>(PlayersInputNumber));
            ReadyToInput(2);
        }else if(PlayersAnswerNumber.size() == 1){
            PlayersAnswerNumber.add(new ArrayList<>(PlayersInputNumber));
            resetViews(true);
        }else{
            if(checkAnswer(PlayersInputNumber, Player)){
                CorrectKey = true;
                showAnswer(Player, new Handler());
            }
            updateHistory(Player);
        }
        // 終了判定
        if(CorrectKey && Player == 2){
            makeResult();
        }else{
            changePlayer(Player);
        }
    }

    public void updateHistory(int player){
        CustomListItem item = new CustomListItem(new ArrayList<>(PlayersInputNumber), HitBlowResult);
        HistoryList.get(player-1).add(item);

        // 出力結果をリストビューに表示
        adapter = new CustomListAdapter(this, R.layout.listview_row, HistoryList.get(player-1));
        Histories.get(player-1).setAdapter(adapter);
    }

    public boolean checkAnswer(List<Integer> input, int player){
        boolean bool = false;
        int tempPlayer, index;
        int NumHit = 0;
        int NumBlow = 0;
        List<Integer> tempAnswer;
        tempPlayer = player==1 ? 2 : 1;
        tempAnswer = PlayersAnswerNumber.get(tempPlayer-1);
        for (int i=0; i<Size; i++){
            index = tempAnswer.indexOf(input.get(i));
            if (index >= 0){
                if (index == i){
                    NumHit++;
                }else{
                    NumBlow++;
                }
            }
        }
        HitBlowResult = new ArrayList<>(Arrays.asList(String.valueOf(NumHit), String.valueOf(NumBlow)));
        if (NumHit==Size){
            bool=true;
        }
        return bool;
    }

    public void showAnswer(int player, Handler hdl) {
        int tempPlayer = player==1 ? 2 : 1;
        Runnable runnable = new Runnable() {
            int counter = 0;
            @Override
            public void run() {
                if (counter < Size) {
                    Log.d("Answer", String.valueOf(PlayersAnswerNumber.get(tempPlayer-1).get(counter)));
                    AnswerNumbersTv.get(tempPlayer-1).get(counter).setText(String.valueOf(PlayersAnswerNumber.get(tempPlayer-1).get(counter)));
                    counter++;
                    hdl.postDelayed(this, 200);
                }
            }
        };
        hdl.postDelayed(runnable, 200);
    }

    public void makeResult(){
        stopTimer();
        animeFade("Finish");
        InputNumView.setVisibility(View.INVISIBLE);
        InputNumScreen.setVisibility(View.INVISIBLE);
        cpi.setVisibility(View.INVISIBLE);
        ElapsedTimeTv.setVisibility(View.INVISIBLE);
        // showAnswer
        showAnswer(1, new Handler());
        showAnswer(2, new Handler());

        //restart
        Restart.setVisibility(View.VISIBLE);
        Restart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                init();
            }
        });
    }

    public void animeFade(String str){
        FadeInOut = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.fade_in_out);
        FadeInOut.setTarget(NotifyTv);

        NotifyTv.setText(str);
        NotifyTv.setVisibility(View.VISIBLE);
        FadeInOut.start();
        FadeInOut.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                NotifyTv.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void tapBack(View view) {
        Intent intent = new Intent(this, HitBlowSettingActivity.class);
        startActivity(intent);
        finish();
    }

    public void tapInfo(View view){
        View layout = getLayoutInflater().inflate( R.layout.custom_alert_dialog2, null );

        AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .create();

        TextView tvTitle = layout.findViewById(R.id.custom_alert_title);
        tvTitle.setText("Forgot your number?");

        TextView tvMessage = layout.findViewById(R.id.custom_alert_message);
        tvMessage.setTextSize(20);
        tvMessage.setText("Tap your \"Question Box\"\nto see your input number");

        Button btnPositive = layout.findViewById(R.id.custom_alert_positive_button);
        btnPositive.setText( "OK" );
        btnPositive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // クリックしたときの処理
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void makeRecall(int tempPlayer){
        View layout = getLayoutInflater().inflate( R.layout.custom_alert_dialog2, null );

        AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .create();

        TextView tvTitle = layout.findViewById(R.id.custom_alert_title);
        tvTitle.setText("Player " + tempPlayer + "'s Number");

        TextView tvMessage = layout.findViewById(R.id.custom_alert_message);
        String NumString = Arrays.toString(PlayersAnswerNumber.get(tempPlayer-1).toArray());
        tvMessage.setText(NumString);

        Button btnPositive = layout.findViewById(R.id.custom_alert_positive_button);
        btnPositive.setText( "OK" );
        btnPositive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // クリックしたときの処理
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void onTimer(){
        startTime = System.currentTimeMillis();
        TimerHandler = new Handler();
        TimerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                TimeRemaining = Math.toIntExact(TimeLimit - elapsedTime / 1000);
                ElapsedTimeTv.setText(String.valueOf(TimeRemaining));
                cpi.setProgressCompat(TimeRemaining, true);
                if(TimeRemaining == 0){
                    TimerHandler.removeCallbacks(TimerRunnable);
                    extraTimer();
                    return;
                }
                TimerHandler.removeCallbacks(TimerRunnable); /* なくても大丈夫...？ */
                TimerHandler.postDelayed(this, 100);
            }
        };
        TimerHandler.post(TimerRunnable);
    }

    public void stopTimer() {
        TimerHandler.removeCallbacks(TimerRunnable);
    }

    public void extraTimer(){
        cpi.setMax(TimeLimit);
        cpi.setIndicatorDirection(CircularProgressIndicator.INDICATOR_DIRECTION_COUNTERCLOCKWISE);
        cpi.setIndicatorColor(Color.RED);
        cpi.setProgressCompat(0, false);

        TimerHandler = new Handler();
        startTime = System.currentTimeMillis();
        TimerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                TimeRemaining = Math.toIntExact(elapsedTime / 1000);
                ElapsedTimeTv.setText(String.valueOf(-TimeRemaining));
                cpi.setProgressCompat(TimeRemaining, true);
                if(TimeRemaining == TimeLimit){
                    TimerHandler.removeCallbacks(TimerRunnable);
                    return;
                }
                TimerHandler.removeCallbacks(TimerRunnable); /* なくても大丈夫...？ */
                TimerHandler.postDelayed(this, 100);
            }
        };
        TimerHandler.post(TimerRunnable);
    }
}
