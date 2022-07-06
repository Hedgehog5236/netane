package com.example.netane;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences.Editor;
import com.google.common.collect.Lists;
import com.google.common.base.Functions;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
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
    TextView topic, topic_title, annotation, answer, num_topics;
    Button next, hide, left_up, right_up, left_bottom, right_bottom;
    View divider;
    int count, len, firstIndex, resourceId, select_genre_index, genre_index, select_topic_index, topic_index, choice_num;
    String Tag, key;
    String[] genre, psychology_choices, psychology_answer;
    TypedArray seeds, genre_name, quiz_answer;
    List<List<Integer>> candidates;
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
        next = findViewById(R.id.next_topic);
        hide = findViewById(R.id.hide_topic);
        num_topics = findViewById(R.id.theme_header_right);

        // ネタ帳(ジャンル→ネタ 二次元配列)読み込み
        seeds = getResources().obtainTypedArray(R.array.seeds);
        // ジャンル名一覧
        genre_name = getResources().obtainTypedArray(R.array.genre_name);
        // クイズの答え一覧
        quiz_answer = getResources().obtainTypedArray(R.array.QuizAnswer);
        //


        // 設定値を読み込み
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        Log.d("preference", String.valueOf(sharedPreferences.getAll()));

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
            Log.d("preference", "preference file was made");
        }

        // 初期化
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 画面に戻った際の挙動
    }

    public void init() {
        // カウント，ジャンルの最初のindex番号カウントを初期化
        count =0; firstIndex = 0;
        // topic選択候補リスト, 選択肢が残っているジャンル一覧リスト, セット型の仮変数を初期化
        candidates = new ArrayList<>();
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
                next.setVisibility(View.VISIBLE);
                hide.setVisibility(View.VISIBLE);
                screen.setClickable(false);
                num_topics.setText("Topics:" + count);
                next.performClick(); // nextボタンを押したことにする
            }
        });
    }

    public void tapBack(View view) {
        Intent intent = new Intent(ThemeActivity.this, MainActivity.class);
        startActivity(intent);
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
        // topic以外を非表示
        annotation.setVisibility(View.INVISIBLE);
        answer.setVisibility(View.INVISIBLE);
        left_up.setVisibility(View.INVISIBLE);
        right_up.setVisibility(View.INVISIBLE);
        left_bottom.setVisibility(View.INVISIBLE);
        right_bottom.setVisibility(View.INVISIBLE);
        screen.setClickable(false);

        // ランダム表示
//        viewRandom();
//        Log.d("Random_show", "Finish");
        // topicを表示
        showTopic();
        Log.d("Topic_show", "Finish");

    }

    public void showTopic(){
        if (genre_index_candidates.size() != 0){
            // topicを選択
            select_genre_index =  (int) (Math.random() * (genre_index_candidates.size())); // 選択肢が残っているジャンルのリストの何番目の数字を選択するか
            genre_index = genre_index_candidates.get(select_genre_index); // 上で選んた数字番目にあるジャンルの番号を，ジャンルをまとめたリストにおけるindexとする
            select_topic_index = (int) (Math.random() * candidates.get(genre_index).size()); // 選択されたジャンルの中の何番目のtopicを選択するのか
            topic_index = candidates.get(genre_index).get(select_topic_index); // 上で選んだ数字番目にあるtopicの番号を，ジャンル別topicリストに於けるindexとする

            // 二次元配列の子要素のリソースIDを取得
            resourceId = seeds.getResourceId(genre_index, 0);
            // 子要素配列を取得
            genre = getResources().getStringArray(resourceId);

//            // 残っているtopicのkeyのindex一覧を表示
//            Log.d("candidates", String.valueOf(candidates));
//            // 選択肢が残っているジャンルのindex番号一覧を表示
//            Log.d("genre of choices", String.valueOf(genre_index_candidates));
//            // 選ばれたtopicの二次元index
//            Log.d("choice index", "[" + String.valueOf(genre_index) + ": " + genre_name.getString(genre_index) + ", " + String.valueOf(topic_index) + "]");
//            // 選ばれたtopicを表示
            Log.d("select", genre[topic_index]);

            // topicを表示
            topic_title.setText(genre_name.getString(genre_index));
            topic.setText(genre[topic_index]);
            // topic選択候補から削除
            candidates.get(genre_index).remove(select_topic_index);
            // ジャンル選択候補から空になったジャンルの候補index番号を削除
            if (candidates.get(genre_index).size() == 0) {
                genre_index_candidates.remove(select_genre_index);
            }

            // もしtopicがQuizなら
            if (genre_name.getString(genre_index).equals("Quiz")){
                annotation.setVisibility(View.VISIBLE);
                annotation.setText("Tap screen to see the answer");
                screen.setClickable(true);
                screen.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new AlertDialog.Builder(ThemeActivity.this)
                                .setTitle("Answer will be shown.")
                                .setMessage("Press OK if you want to see the answer")
                                .setPositiveButton("OK", new  DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // OKをクリックしたときの処理
                                        answer.setVisibility(View.VISIBLE);
                                        annotation.setText("Answer");
                                        answer.setText(quiz_answer.getString(topic_index));
                                        screen.setClickable(false);
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
                Log.d("Psychology", String.valueOf(psychology_choices.length));
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
        }else {
            // 選択肢がない場合
            topic.setText("That is all!\nTap screen to reset");
            divider.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            hide.setVisibility(View.INVISIBLE);
            Log.d("Finish", "That is all");

            // reset
            screen.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    topic.setText("Tap screen to start");
                    screen.setClickable(false);
                    Log.d(Tag, "Reset");
                    init();
                }
            });
        }
        // count表示を更新
        num_topics.setText("Topics:" + count--);
    }

    public void viewRandom(){
        handler = new Handler();
        r = new Runnable() {
            int counter = 0;
            @Override
            public void run() {
                // UIスレッド
                counter++;
                if (counter > 10) { // 5回実行したら終了
                    return;
                }
                int random_genre_index = (int) (Math.random() * (seeds.length()));
                String random_genre_name = genre_name.getString(random_genre_index);
                int resId = getResources().getIdentifier(random_genre_name, "array", getPackageName());
                String[] strArray = getResources().getStringArray(resId);
                int random_topic_index = (int) (Math.random() * (strArray.length));
                Log.d("[R_genre(N), R_topic]", String.valueOf(random_genre_index) + "(" + random_genre_name + ")" + ", " + String.valueOf(random_topic_index));

                String random_topic = strArray[random_topic_index];
                topic_title.setText(random_genre_name);
                topic.setText(random_topic);
                handler.postDelayed(this, 100);
            }
        };
        handler.post(r);
    }

    // 四択選択で押されたボタンごとの処理
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
        annotation.setText("Tap screen to return to the selection");
        screen.setClickable(true);
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // topicを表示
                topic_title.setText(genre_name.getString(genre_index));
                topic.setText(genre[topic_index]);
                // 答えを非表示
                answer.setVisibility(View.INVISIBLE);
                // スクリーンタップで戻るを非表示
                annotation.setVisibility(View.INVISIBLE);
                // screenをタップ不可に設定
                screen.setClickable(false);
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
