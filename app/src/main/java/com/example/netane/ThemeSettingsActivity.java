package com.example.netane;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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

    public void tapBack(View view) {
        Intent intent = new Intent(ThemeSettingsActivity.this, ThemeActivity.class);
        startActivity(intent);
    }
}