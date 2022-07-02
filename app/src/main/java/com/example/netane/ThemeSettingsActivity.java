package com.example.netane;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class ThemeSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new ThemeSettingsFragment())
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void tapBack(View view) {
        Intent intent = new Intent(ThemeSettingsActivity.this, ThemeActivity.class);
        startActivity(intent);
    }
}