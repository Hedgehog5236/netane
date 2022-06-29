package com.example.netane;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class ThemeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "MyActivity";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        // 変数セット
        TextView Topic = findViewById(R.id.topic_tv);
        // ネタ帳読み込み
        TypedArray seeds = getResources().obtainTypedArray(R.array.seeds);
        // ジャンル名一覧
        TypedArray genre_name = getResources().obtainTypedArray(R.array.genre_name);

        // 設定値を読み込み
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);

        // 表示設定にチェックがあるものを配列にセット
        for (int i=0; i < seeds.length(); i++) {
            // 二次元配列の子要素のリソースIDを取得
//            int resourceId = seeds.getResourceId(i, 0);
            // 子要素配列を取得
//            String[] genre = getResources().getStringArray(resourceId);

//            Set<String> selections = sharedPreferences.getStringSet(genre_name.getString(i), null);
//            String selections = sharedPreferences.getString(genre_name.getString(i), "");
            Set<String> temp = new HashSet<String>();
            Log.d(TAG, String.valueOf(sharedPreferences.getStringSet(genre_name.getString(i)+"Individual", temp)));

        }
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

    }

    public void tapNext(View view) {

    }

}
