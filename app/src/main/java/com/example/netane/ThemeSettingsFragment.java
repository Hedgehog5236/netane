package com.example.netane;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ThemeSettingsFragment extends PreferenceFragmentCompat {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Context context = getPreferenceManager().getContext();

        int count=0;

        // RootのPreferenceScreenとPreferenceを管理しているPreferenceManagerを取得
        PreferenceScreen root = getPreferenceScreen();

        // ネタ帳
        TypedArray seeds = getResources().obtainTypedArray(R.array.seeds);
        // ジャンル名一覧
        TypedArray genre_name = getResources().obtainTypedArray(R.array.genre_name);

        // カテゴリを作成&追加
        PreferenceCategory category_genre = new PreferenceCategory(root.getContext());
        category_genre.setTitle("ジャンル別 表示設定");
        root.addPreference(category_genre);    //Preferenceのrootにcategoryを追加

        // カテゴリに含めるPreferenceを作成&追加
        for (int i=0; i < genre_name.length(); i++){
            SwitchPreferenceCompat TempSwitch = new SwitchPreferenceCompat(context);
            TempSwitch.setTitle(genre_name.getString(i));
            TempSwitch.setKey(genre_name.getString(i));
            TempSwitch.setChecked(true);
            category_genre.addPreference(TempSwitch); //categoryにPreferenceを追加
        }

        // カテゴリを作成&追加
        PreferenceCategory category_individual = new PreferenceCategory(root.getContext());
        category_individual.setTitle("個別 表示設定");
        root.addPreference(category_individual);    //Preferenceのrootにcategoryを追加

        // カテゴリに含めるPreferenceを作成&追加
        for (int i=0; i < genre_name.length(); i++) {
            // 二次元配列の子要素のリソースIDを取得
            int resourceId = seeds.getResourceId(i, 0);

            // 子要素配列を取得
            String[] genre = getResources().getStringArray(resourceId);

            // EntryValuesに設定する値を準備する
            int len = genre.length;
            int[] intArray = IntStream.range(count, count + len).toArray();
//            int配列からstring配列に変換
            String[] strArray = Arrays.stream(intArray)
                    .mapToObj(String::valueOf)
                    .toArray(String[]::new);

            // 次のgenreのEntryValuesのスタート番号を設定
            count += len;

            // マルチセレクトリストを作成&追加
            MultiSelectListPreference TempIndividualSelect = new MultiSelectListPreference(context);
            TempIndividualSelect.setTitle(genre_name.getString(i));
            TempIndividualSelect.setKey(genre_name.getString(i)+"Individual");
            TempIndividualSelect.setEntries(genre);
            TempIndividualSelect.setEntryValues(strArray);
            category_individual.addPreference(TempIndividualSelect); //categoryにPreferenceを追加
        }
    }
}