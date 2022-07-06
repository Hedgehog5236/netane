package com.example.netane;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

        // 一括表示カテゴリ
        PreferenceCategory category_all = new PreferenceCategory(root.getContext());
        category_all.setTitle("一括 表示設定");
        root.addPreference(category_all);    //Preferenceのrootにcategoryを追加

        // ジャンル一括表示設定を追加
        SwitchPreferenceCompat genre_all = new SwitchPreferenceCompat(context);
        genre_all.setTitle("全てのジャンルをON/OFF");
        genre_all.setSummaryOff("Turn On to Enable all genre");
        genre_all.setSummaryOn("Turn On to Disable all genre");
        genre_all.setKey("AllGenre");
        genre_all.setChecked(true);
        category_all.addPreference(genre_all);

        // 個別一括表示設定を追加
        SwitchPreferenceCompat individual_all = new SwitchPreferenceCompat(context);
        individual_all.setTitle("全てのTopicをON/OFF");
        individual_all.setSummaryOff("Turn On to Enable all topic");
        individual_all.setSummaryOn("Turn On to Disable all topic");
        individual_all.setKey("AllIndividual");
        individual_all.setChecked(true);
        category_all.addPreference(individual_all);

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
            TempIndividualSelect.setDefaultValue(items);
            category_individual.addPreference(TempIndividualSelect); //categoryにPreferenceを追加
            TempIndividualSelect.setDependency(genre_name.getString(i)); // categoryにPreferenceを追加した後でdependencyを追加しないとエラーが出る！！
        }

        // ジャンル一括表示設定
        genre_all.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                new AlertDialog.Builder(getContext())
                        .setTitle("All genre setting")
                        .setMessage(genre_all.isChecked() ? "All genre will be enable" : "All genre will be disable")
                        .setPositiveButton("OK", new  DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OKをクリックしたときの処理
                                for (int i=0; i < genre_name.length(); i++) {
                                    SwitchPreferenceCompat TempSwitch = findPreference(genre_name.getString(i));
                                    TempSwitch.setChecked(genre_all.isChecked());
                                }
                            }
                        })

                        .setNeutralButton("Cancel", new  DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancelをクリックしたときの処理
                                genre_all.setChecked(!genre_all.isChecked());
                            }
                        })
                        .show();
                return false;
            }
        });

        // 個別一括表示設定
        individual_all.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                new AlertDialog.Builder(getContext())
                        .setTitle("All topic setting")
                        .setMessage(individual_all.isChecked() ? "All topic will be enable" : "All topic will be disable")
                        .setPositiveButton("OK", new  DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OKをクリックしたときの処理
                                int count = 0;
                                for (int i=0; i < genre_name.length(); i++) {
                                    MultiSelectListPreference TempIndividualSelect = findPreference(genre_name.getString(i)+"Individual");

                                    // 二次元配列の子要素のリソースIDを取得
                                    int resourceId = seeds.getResourceId(i, 0);

                                    // 子要素配列を取得
                                    String[] temp_genre = getResources().getStringArray(resourceId);
                                    // 表示枠に収めるためにセットした文字列内の改行を，MultiSelectListPreferenceでは取り払って表示するための操作
                                    String[] genre = Arrays.stream(temp_genre).map(s -> s.replace("\n", "")).collect(Collectors.toList()).toArray(new String[temp_genre.length]);

                                    // EntryValuesに設定する値を準備する
                                    int len = genre.length;
                                    int[] intArray = IntStream.range(count, count + (len * (individual_all.isChecked() ? 1 : 0))).toArray();

                                    // int配列からstring配列に変換
                                    String[] strArray = Arrays.stream(intArray)
                                            .mapToObj(String::valueOf)
                                            .toArray(String[]::new);

                                    // 次のgenreのEntryValuesのスタート番号を設定
                                    count += len;

                                    Set<String> items = new HashSet<>(Arrays.asList(strArray));
                                    TempIndividualSelect.setValues(items);
                                }
                            }
                        })

                        .setNeutralButton("Cancel", new  DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancelをクリックしたときの処理
                                individual_all.setChecked(!individual_all.isChecked());
                            }
                        })
                        .show();
                return false;
            }
        });
    }
}