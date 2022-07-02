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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThemeSettingsFragment extends PreferenceFragmentCompat {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // 変数
        int count = 0; //質問の数
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Context context = getPreferenceManager().getContext();

        // RootのPreferenceScreenとPreferenceを管理しているPreferenceManagerを取得
        PreferenceScreen root = getPreferenceScreen();

        // ネタ帳
        TypedArray seeds = getResources().obtainTypedArray(R.array.seeds);
        // ジャンル名一覧
        TypedArray genre_name = getResources().obtainTypedArray(R.array.genre_name);

        // ジャンル別表示カテゴリ
        PreferenceCategory category_genre = new PreferenceCategory(root.getContext());
        category_genre.setTitle("ジャンル別 表示設定");
        root.addPreference(category_genre);    //Preferenceのrootにcategoryを追加

        for (int i=0; i < genre_name.length(); i++) {
            SwitchPreferenceCompat TempSwitch = new SwitchPreferenceCompat(context);
            TempSwitch.setTitle(genre_name.getString(i));
            TempSwitch.setKey(genre_name.getString(i));
            TempSwitch.setChecked(true);
            category_genre.addPreference(TempSwitch); //categoryにPreferenceを追加
        }

        // 個別表示カテゴリ
        PreferenceCategory category_individual = new PreferenceCategory(root.getContext());
        category_individual.setTitle("個別 表示設定");
        root.addPreference(category_individual);    //Preferenceのrootにcategoryを追加

        for (int i=0; i < genre_name.length(); i++) {
            // 二次元配列の子要素のリソースIDを取得
            int resourceId = seeds.getResourceId(i, 0);

            // 子要素配列を取得
            String[] temp_genre = getResources().getStringArray(resourceId);
            // 表示枠に収めるためにセットした文字列内の改行を，MultiSelectListPreferenceでは取り払って表示するための操作
            String[] genre = Arrays.stream(temp_genre).map(s -> s.replace("\n", "")).collect(Collectors.toList()).toArray(new String[temp_genre.length]);

            // EntryValuesに設定する値を準備する
            int len = genre.length;
            int[] intArray = IntStream.range(count, count + len).toArray();

            // int配列からstring配列に変換
            String[] strArray = Arrays.stream(intArray)
                    .mapToObj(String::valueOf)
                    .toArray(String[]::new);

            // 次のgenreのEntryValuesのスタート番号を設定
            count += len;

            // マルチセレクトリスト
            MultiSelectListPreference TempIndividualSelect = new MultiSelectListPreference(context);
            TempIndividualSelect.setTitle(genre_name.getString(i));
            TempIndividualSelect.setKey(genre_name.getString(i)+"Individual");
            TempIndividualSelect.setEntries(genre);
            TempIndividualSelect.setEntryValues(strArray);
            Set<String> items = new HashSet<>(Arrays.asList(strArray));
//            TempIndividualSelect.setDefaultValue(items);
            category_individual.addPreference(TempIndividualSelect); //categoryにPreferenceを追加
            TempIndividualSelect.setDependency(genre_name.getString(i)); // categoryにPreferenceを追加した後でdependencyを追加しないとエラーが出る！！
        }
    }
}