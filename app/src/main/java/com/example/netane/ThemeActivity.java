package com.example.netane;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences.Editor;
import com.google.common.collect.Lists;
import com.google.common.base.Functions;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ThemeActivity extends AppCompatActivity {
    // 変数
    SharedPreferences sharedPreferences;
    androidx.constraintlayout.widget.ConstraintLayout screen;
    TextView caution, answer_header, topic, topic_title, annotation, answer, num_topics;
    ImageView topicImage, answerImage;
    Button next, hide, left_up, right_up, left_bottom, right_bottom;
    View divider;
    int ImageNameEndIndex, CautionStrEndIndex, count, len, firstIndex, resourceId, select_genre_index, genre_index, select_topic_index, topic_index, choice_num;
    String Tag, key;
    String[] genre, psychology_choices, psychology_answer, str_divided;
    TypedArray seeds, genre_name, quiz_answer;
    List<List<Integer>> candidates, displayCompList;
    List<Integer> genre_index_candidates, IntList;
    List<String> temp_list;
    Set<String> value, temp;
    Handler handler;
    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        Tag = "ThemeActivity";

        answer_header = findViewById(R.id.Answer_header);
        caution = findViewById(R.id.caution);
        divider = findViewById(R.id.divider1);
        answer = findViewById(R.id.Answer);
        left_up = findViewById(R.id.left_up);
        right_up = findViewById(R.id.right_up);
        left_bottom = findViewById(R.id.left_bottom);
        right_bottom = findViewById(R.id.right_bottom);
        topic = findViewById(R.id.topic_tv);
        topic_title = findViewById(R.id.topic_title);
        annotation = findViewById(R.id.annotation);
        screen = findViewById(R.id.theme_screen);
        topicImage = findViewById(R.id.topic_iv);
        answerImage = findViewById(R.id.Answer_iv);
        next = findViewById(R.id.next_topic);
        hide = findViewById(R.id.hide_topic);
        num_topics = findViewById(R.id.theme_header_right);

        // スクロール可能にする
        topic.setMovementMethod(new ScrollingMovementMethod());
        answer.setMovementMethod(new ScrollingMovementMethod());

        // ネタ帳(ジャンル→ネタ 二次元配列)読み込み
        seeds = getResources().obtainTypedArray(R.array.seeds);
        // ジャンル名一覧
        genre_name = getResources().obtainTypedArray(R.array.genre_name);
        // クイズの答え一覧
        quiz_answer = getResources().obtainTypedArray(R.array.QuizAnswer);

        // 設定値を読み込み
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);

        // 初回起動時，preferenceファイルが存在しないので作成する
        if(sharedPreferences.getAll().isEmpty()) {
            Editor makePreference = sharedPreferences.edit();
            count = 0;
            for (int i=0; i < seeds.length(); i++) {
                key = genre_name.getString(i) + "Individual";
                resourceId = seeds.getResourceId(i, 0);
                len = getResources().getStringArray(resourceId).length;
                IntList = IntStream.range(count, count + len)
                        .boxed().collect(Collectors.toList());
                List<String> temp_value = Lists.transform(IntList, Functions.toStringFunction());
                value = new HashSet<String>(temp_value);
                makePreference.putStringSet(key, value);
                count = count + len;
                makePreference.apply();
            }
        }
        // 初期化
        init();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        // 画面に戻った際の挙動
        if (genre == null){
            init();
            return;
        }
        // カウント，ジャンルの最初のindex番号カウントを初期化
        count = 0;
        firstIndex = 0;
        // topic選択候補リスト, 選択肢が残っているジャンル一覧リスト, セット型の仮変数を初期化
        candidates = new ArrayList<>();
        genre_index_candidates = new ArrayList<>();
        temp = new HashSet<String>();

        // 表示設定にチェックがあるものを配列にセット
        for (int i = 0; i < seeds.length(); i++) {
            // もしジャンル別表示設定でオフになっていなければ，
            if (sharedPreferences.getBoolean(genre_name.getString(i), true)) {
                // list<String>で保存された設定値を読み込む． → list<Integer>に型変換 and 全ての要素にインデックス番号の調整処理を実行する.
                temp_list = new ArrayList<>(sharedPreferences.getStringSet(genre_name.getString(i) + "Individual", temp));
                IntList = temp_list.stream().map(e -> Integer.parseInt(e) - firstIndex).collect(Collectors.toList());
                // すでに表示されたものが設定値に含まれていれば削除
                boolean result = IntList.removeAll(displayCompList.get(i));
                candidates.add(IntList);
                // 選択肢が残っているジャンルのindex番号一覧
                if (IntList.size() != 0) {
                    genre_index_candidates.add(i);
                }
                // もしオフなら空のリストを入れる
            } else {
                IntList = new ArrayList<>();
                candidates.add(IntList);
            }
            // countを更新
            count = count + IntList.size();
            resourceId = seeds.getResourceId(i, 0);
            firstIndex = firstIndex + getResources().getStringArray(resourceId).length;
        }

        // 表示再開
        resetViews();

        //topicを表示
        showTopic();
    }

    public void init() {
        // カウント，ジャンルの最初のindex番号カウントを初期化
        count =0; firstIndex = 0;
        // topic選択候補リスト, 選択肢が残っているジャンル一覧リスト, セット型の仮変数を初期化
        candidates = new ArrayList<>();
        displayCompList = new ArrayList<>();
        genre_index_candidates = new ArrayList<>();
        temp = new HashSet<String>();

        // 表示設定にチェックがあるものを配列にセット
        for (int i=0; i < seeds.length(); i++) {
            // もしジャンル別表示設定でオフになっていなければ，
            if(sharedPreferences.getBoolean(genre_name.getString(i), true)) {
                // list<String>で保存された設定値を読み込む． → list<Integer>に型変換 and 全ての要素にインデックス番号の調整処理を実行する.
                temp_list = new ArrayList<>(sharedPreferences.getStringSet(genre_name.getString(i)+"Individual", temp));
                IntList = temp_list.stream().map(e -> Integer.parseInt(e) - firstIndex).collect(Collectors.toList());
                candidates.add(IntList);
                // 選択肢が残っているジャンルのindex番号一覧
                genre_index_candidates.add(i);
            // もしオフなら空のリストを入れる
            }else {
                IntList = new ArrayList<>();
                candidates.add(IntList);
            }
            // すでに表示したものリストをついでに初期化
            displayCompList.add(new ArrayList<Integer>(0));
            // countを更新
            count = count + IntList.size();
            resourceId = seeds.getResourceId(i, 0);
            firstIndex = firstIndex + getResources().getStringArray(resourceId).length;
        }

        // 開始表示
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                topic_title.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                screen.setClickable(false);
                num_topics.setText("Topics:" + count--);
                next.performClick(); // nextボタンを押したことにする
            }
        });
    }

    public void tapBack(View view) {
        Intent intent = new Intent(ThemeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void tapSetting(View view) {
        Intent intent = new Intent(ThemeActivity.this, ThemeSettingsActivity.class);
        startActivity(intent);
    }

    public void tapHide(View view) {
        new AlertDialog.Builder(this)
            .setTitle("The following topic will be hidden")
            .setMessage(genre[topic_index].replace("\n", ""))
            .setPositiveButton("OK", new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // OKをクリックしたときの処理
                    Editor editor = sharedPreferences.edit();
                    key = genre_name.getString(genre_index)+"Individual";
                    List<String> temp_value = new ArrayList<>(sharedPreferences.getStringSet(key, temp));
                    temp_value.remove(select_topic_index);
                    value = new HashSet<String>(temp_value);
                    editor.putStringSet(key, value);
                    editor.apply();
                    // hideに設定
                    Log.d("Hide", "[" + genre_index + "," + topic_index + "]: " + genre[topic_index]);
                    next.performClick();
                }
            })

            .setNeutralButton("Cancel", new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Cancelをクリックしたときの処理
                    Log.d("Hide", "Cancel");
                }
            })
            .show();
    }

    public void tapNext(View view) {
        // viewの表示やボタンを初期化
        resetViews();
        // topicの表示
        UpToDateTextView();
    }

    public void resetViews(){
        topic.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
        // topicと仕切り棒以外を非表示
        caution.setVisibility(View.INVISIBLE);
        topicImage.setVisibility(View.INVISIBLE);
        annotation.setVisibility(View.INVISIBLE);
        answer.setVisibility(View.INVISIBLE);
        answerImage.setVisibility(View.INVISIBLE);
        answer_header.setVisibility(View.INVISIBLE);
        left_up.setVisibility(View.INVISIBLE);
        right_up.setVisibility(View.INVISIBLE);
        left_bottom.setVisibility(View.INVISIBLE);
        right_bottom.setVisibility(View.INVISIBLE);
        // next, hide以外のボタン押下不可
        annotation.setClickable(false);
        screen.setClickable(false);
    }

    // topicTextViewの更新・終了判定
    public void UpToDateTextView(){
        if (genre_index_candidates.size() != 0) {
            handler = new Handler();
            r = new Runnable() {
                int counter = 0;

                @Override
                public void run() {
                    // UIスレッド
                    if (counter < 10) { // ランダム表示
                        showRandom();
                        counter++;
                        handler.postDelayed(this, 100);
                    } else { // topicの正規表示
                        selectTopic();
                        showTopic();
                        // topic候補リストなどを更新
                        UpToDateCandidates();
                    }
                }
            };
            handler.post(r);
        }else {
            // 選択肢がない場合
            topic_title.setVisibility(View.INVISIBLE);
            topic.setText("That is all!\nTap screen to reset");
            divider.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            hide.setVisibility(View.INVISIBLE);

            // reset
            screen.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    topic.setText("Tap screen to start");
                    screen.setClickable(false);
                    init();
                }
            });
        }
    }

    public void showRandom(){
        hide.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        int random_genre_index = (int) (Math.random() * (seeds.length()));
        String random_genre_name = genre_name.getString(random_genre_index);
        int resId = getResources().getIdentifier(random_genre_name, "array", getPackageName());
        String[] strArray = getResources().getStringArray(resId);
        int random_topic_index = (int) (Math.random() * (strArray.length));
        String random_topic = strArray[random_topic_index];
        topic_title.setText(random_genre_name);
        topic.setText(random_topic);
    }

    public void selectTopic(){
        // topicを選択
        select_genre_index =  (int) (Math.random() * (genre_index_candidates.size())); // 選択肢が残っているジャンルのリストの何番目の数字を選択するか
        genre_index = genre_index_candidates.get(select_genre_index); // 上で選んた数字番目にあるジャンルの番号を，ジャンルをまとめたリストにおけるindexとする
        select_topic_index = (int) (Math.random() * candidates.get(genre_index).size()); // 選択されたジャンルの中の何番目のtopicを選択するのか
        topic_index = candidates.get(genre_index).get(select_topic_index); // 上で選んだ数字番目にあるtopicの番号を，ジャンル別topicリストに於けるindexとする

        // 二次元配列の子要素のリソースIDを取得
        resourceId = seeds.getResourceId(genre_index, 0);
        // 子要素配列を取得
        genre = getResources().getStringArray(resourceId);
    }

    public void showTopic(){
        // ボタンを表示
        hide.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);

        // 選ばれたtopicを表示
        Log.d("selected_topic", genre[topic_index]);
        topic_title.setText(genre_name.getString(genre_index));
        // 画像やcautionがあればそれに合わせてviewを表示
        str_divided = divideStr(genre[topic_index]);
        /* 画像があるパターン */
        if(str_divided[0].length() != 0) {
            topic.setVisibility(View.INVISIBLE);
            topicImage.setVisibility(View.VISIBLE);
            int id = getResources().getIdentifier(str_divided[0], "drawable", getPackageName());
            topicImage.setImageResource(id);
        /* 普通のテキストのパターン */
        }else {
            topic.setText(str_divided[2]);
        }
        /* cautionがあるパターン */
        if(str_divided[1].length() != 0){
            caution.setVisibility(View.VISIBLE);
            caution.setText(str_divided[1]);
        }

        // もしtopicがQuizなら
        if (genre_name.getString(genre_index).equals("Quiz")){
            annotation.setVisibility(View.VISIBLE);
            annotation.setText("Tap here to see the answer");
            annotation.setClickable(true);
            annotation.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(ThemeActivity.this)
                            .setTitle("Answer will be shown.")
                            .setMessage("Press OK if you want to see the answer")
                            .setPositiveButton("OK", new  DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // OKをクリックしたときの処理
                                    annotation.setText("Answer");
                                    annotation.setClickable(false);
                                    // 答えが画像ならそれに合わせてviewを表示
                                    str_divided = divideStr(quiz_answer.getString(topic_index));
                                    /* 画像が答えのパターン */
                                    if(str_divided[0].length() != 0){
                                        answer.setVisibility(View.INVISIBLE);
                                        answerImage.setVisibility(View.VISIBLE);
                                        int id = getResources().getIdentifier(str_divided[0], "drawable", getPackageName());
                                        answerImage.setImageResource(id);
                                    /* 答えの詳細があるパターン */
                                    }else if (str_divided[2].length() != 0){
                                        answer.setVisibility(View.VISIBLE);
                                        answer.setText(str_divided[2]);
                                    }
                                    /* 答えの文字列があるパターン */
                                    if(str_divided[1].length() != 0){
                                        answer_header.setVisibility(View.VISIBLE);
                                        answer_header.setText(str_divided[1]);
                                    }
                                }
                            })

                            .setNeutralButton("Cancel", new  DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Cancelをクリックしたときの処理
                                }
                            })
                            .show();
                }
            });
        }
        // もしtopicがPsychologyなら
        if (genre_name.getString(genre_index).equals("Psychology")){
            // 選択肢を読み込み
            resourceId = getResources().getIdentifier("Psychology"+topic_index+"_choices", "array", getPackageName());
            psychology_choices = getResources().getStringArray(resourceId);
            // 答えを読み込み
            resourceId = getResources().getIdentifier("Psychology"+topic_index+"_answer", "array", getPackageName());
            psychology_answer = getResources().getStringArray(resourceId);
            // 選択肢の数分ボタンを表示
            switch (psychology_choices.length){
                case (1):{
                    left_up.setVisibility(View.VISIBLE);
                    left_up.setText(psychology_choices[0]);
                    break;
                }
                case (2):{
                    left_up.setVisibility(View.VISIBLE);
                    right_up.setVisibility(View.VISIBLE);
                    left_up.setText(psychology_choices[0]);
                    right_up.setText(psychology_choices[1]);
                    break;
                }
                case (3):{
                    left_up.setVisibility(View.VISIBLE);
                    right_up.setVisibility(View.VISIBLE);
                    left_bottom.setVisibility(View.VISIBLE);
                    left_up.setText(psychology_choices[0]);
                    right_up.setText(psychology_choices[1]);
                    left_bottom.setText(psychology_choices[2]);
                    break;
                }
                case (4):{
                    left_up.setVisibility(View.VISIBLE);
                    right_up.setVisibility(View.VISIBLE);
                    left_bottom.setVisibility(View.VISIBLE);
                    right_bottom.setVisibility(View.VISIBLE);
                    left_up.setText(psychology_choices[0]);
                    right_up.setText(psychology_choices[1]);
                    left_bottom.setText(psychology_choices[2]);
                    right_bottom.setText(psychology_choices[3]);
                    break;
                }
            }
        }
        // count表示を更新
        num_topics.setText("Topics:" + count--);
    }

    public void UpToDateCandidates(){
        // topic選択候補から削除
        candidates.get(genre_index).remove(select_topic_index);
        // 表示したものリストにindexを追加
        displayCompList.get(genre_index).add(topic_index);
        // 昇順にソート
        Collections.sort(displayCompList.get(genre_index));
        // ジャンル選択候補から空になったジャンルの候補index番号を削除
        if (candidates.get(genre_index).size() == 0) {
            genre_index_candidates.remove(select_genre_index);
        }
    }

    public String[] divideStr(String text){
        String ImageName, CautionStr, mainStr;
        ImageNameEndIndex = text.indexOf("*im*");
        CautionStrEndIndex = text.indexOf("*str*");
        if (ImageNameEndIndex != -1 && CautionStrEndIndex != -1){
            ImageName = text.substring(0, ImageNameEndIndex);
            CautionStr = text.substring(ImageNameEndIndex+4, CautionStrEndIndex);
            mainStr = text.substring(CautionStrEndIndex+5);
        }
        else if(ImageNameEndIndex != -1){
            ImageName = text.substring(0, ImageNameEndIndex);
            CautionStr = "";
            mainStr = text.substring(ImageNameEndIndex+4);
        }
        else if(CautionStrEndIndex != -1){
            ImageName = "";
            CautionStr = text.substring(0, CautionStrEndIndex);
            mainStr = text.substring(CautionStrEndIndex+5);
        }
        else{
            ImageName = "";
            CautionStr = "";
            mainStr = text;
        }
        return new String[]{ImageName, CautionStr, mainStr};
    }

    // 四択で押されたボタンごとの処理
    public void tapChoice(View view) {
        switch (view.getId()){
            case (R.id.left_up):{
                choice_num = 0;
                break;
            }
            case (R.id.right_up):{
                choice_num = 1;
                break;
            }
            case (R.id.left_bottom):{
                choice_num = 2;
                break;
            }
            case (R.id.right_bottom):{
                choice_num = 3;
                break;
            }
        }
        //選択肢を非表示
        left_up.setVisibility(View.INVISIBLE);
        right_up.setVisibility(View.INVISIBLE);
        left_bottom.setVisibility(View.INVISIBLE);
        right_bottom.setVisibility(View.INVISIBLE);
        // 答えを表示
        answer.setVisibility(View.VISIBLE);
        answer.setText(psychology_answer[choice_num]);
        // スクリーンタップで戻る
        annotation.setVisibility(View.VISIBLE);
        annotation.setText("Tap here to return to the selection");
        annotation.setClickable(true);
        annotation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // topicを表示
                topic_title.setText(genre_name.getString(genre_index));
                topic.setText(genre[topic_index]);
                // 答えを非表示
                answer.setVisibility(View.INVISIBLE);
                // annotation tapで戻るを非表示
                annotation.setVisibility(View.INVISIBLE);
                // annotationをタップ不可に設定
                annotation.setClickable(false);
                // 選択肢を表示
                switch (psychology_choices.length){
                    case (1):{
                        left_up.setVisibility(View.VISIBLE);
                        left_up.setText(psychology_choices[0]);
                        break;
                    }
                    case (2):{
                        left_up.setVisibility(View.VISIBLE);
                        right_up.setVisibility(View.VISIBLE);
                        left_up.setText(psychology_choices[0]);
                        right_up.setText(psychology_choices[1]);
                        break;
                    }
                    case (3):{
                        left_up.setVisibility(View.VISIBLE);
                        right_up.setVisibility(View.VISIBLE);
                        left_bottom.setVisibility(View.VISIBLE);
                        left_up.setText(psychology_choices[0]);
                        right_up.setText(psychology_choices[1]);
                        left_bottom.setText(psychology_choices[2]);
                        break;
                    }
                    case (4):{
                        left_up.setVisibility(View.VISIBLE);
                        right_up.setVisibility(View.VISIBLE);
                        left_bottom.setVisibility(View.VISIBLE);
                        right_bottom.setVisibility(View.VISIBLE);
                        left_up.setText(psychology_choices[0]);
                        right_up.setText(psychology_choices[1]);
                        left_bottom.setText(psychology_choices[2]);
                        right_bottom.setText(psychology_choices[3]);
                        break;
                    }
                }
            }
        });
    }

}
