package com.example.netane;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    public void Shinkei(View view) {
        Intent Shinkei = new Intent(MainActivity.this,ConcentrationActivity.class);
        startActivity(Shinkei);
    }

    public void HitBlow(View view) {
        Intent hitblow = new Intent(MainActivity.this,HitBlowActivity.class);
        startActivity(hitblow);
    }

    public void Theme(View view) {
        Intent theme_setting = new Intent(MainActivity.this,ThemeActivity.class);
        startActivity(theme_setting);
    }

    public void HighLow(View view) {
        Intent news_setting = new Intent(MainActivity.this,HighLowActivity.class);
        startActivity(news_setting);
    }
}
