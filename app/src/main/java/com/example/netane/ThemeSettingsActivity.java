package com.example.netane;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ThemeSettingsActivity extends AppCompatActivity {
    // 変数
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch allSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

//        allSwitch = findViewById(R.id.settings_header_switch);
//        allSwitch.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new ThemeSettingsFragment())
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void tapBack(View view) {
        finish();
    }
}