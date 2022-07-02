package com.example.netane;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Arrays;
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
    TextView topic, num_topics;
    Button next, hide;
    int count, len, firstIndex, resourceId, select_genre_index, genre_index, select_topic_index, topic_index;
    String Tag, key;
    String[] genre;
    TypedArray seeds, genre_name;
    List<List<Integer>> candidates;
    List<Integer> genre_index_candidates, IntList;
    List<String> temp_list;
    Set<String> value, temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        Tag = "ThemeActivity";

        topic = findViewById(R.id.topic_tv);
        screen = findViewById(R.id.theme_screen);
        next = findViewById(R.id.next_topic);
        hide = findViewById(R.id.hide_topic);
        num_topics = findViewById(R.id.theme_header_right);

        // ネタ帳(ジャンル→ネタ 二次元配列)読み込み
        seeds = getResources().obtainTypedArray(R.array.seeds);
        // ジャンル名一覧
        genre_name = getResources().obtainTypedArray(R.array.genre_name);
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
                topic.setText("Tap screen to start");
                next.setVisibility(View.VISIBLE);
                hide.setVisibility(View.VISIBLE);
                screen.setClickable(false);
                num_topics.setText(count + " more Topics");
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

            // 残っているtopicのkeyのindex一覧を表示
            Log.d("candidates", String.valueOf(candidates));
            // 選択肢が残っているジャンルのindex番号一覧を表示
            Log.d("genre of choices", String.valueOf(genre_index_candidates));
            // 選ばれたtopicの二次元index
            Log.d("choice index", "[" + String.valueOf(genre_index) + ": " + genre_name.getString(genre_index) + ", " + String.valueOf(topic_index) + "]");
            // 選ばれたtopicを表示
            Log.d("select", genre[topic_index]);
            // key
//            Log.d("key", key_list.get(genre_index).get(topic_index));

            // topicを表示
            topic.setText(genre[topic_index]);
            // topic選択候補から削除
            candidates.get(genre_index).remove(select_topic_index);
            // ジャンル選択候補から空になったジャンルの候補index番号を削除
            if (candidates.get(genre_index).size() == 0) {
                genre_index_candidates.remove(select_genre_index);
            }

        }else{
            // 選択肢がない場合
            topic.setText("That is all!\nTap screen to reset");
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
        num_topics.setText(count-- + " more Topics");
    }
}
