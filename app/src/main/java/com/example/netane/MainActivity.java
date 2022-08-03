package com.example.netane;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Puzzle(View view) {
        Intent puzzle_setting = new Intent(MainActivity.this,PuzzleSettingActivity.class);
        startActivity(puzzle_setting);
    }

    public void ColorPalette(View view) {
        Intent color_palette_setting = new Intent(MainActivity.this,ColorPaletteSettingActivity.class);
        startActivity(color_palette_setting);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Concentration(View view) {
        Intent Concentration = new Intent(MainActivity.this,ConcentrationActivity.class);
        startActivity(Concentration);
    }

    public void HitBlow(View view) {
        Intent HitBlow_setting = new Intent(MainActivity.this,HitBlowSettingActivity.class);
        startActivity(HitBlow_setting);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Theme(View view) {
        Intent theme_setting = new Intent(MainActivity.this,ThemeActivity.class);
        startActivity(theme_setting);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void HighLow(View view) {
        Intent news_setting = new Intent(MainActivity.this,HighLowActivity.class);
        startActivity(news_setting);
    }
}
