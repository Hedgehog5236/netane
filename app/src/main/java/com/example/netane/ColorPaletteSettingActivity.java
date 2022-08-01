package com.example.netane;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ColorPaletteSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpalette_setting);
    }

    public void ColorPalette(View view) {
        int num_color, num_quiz;
        switch (view.getId()){
            case R.id.Level1:
                num_color = 1;
                break;
            case R.id.Level2:
                num_color = 2;
                break;
            case R.id.Level3:
                num_color = 3;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
        Intent intent = new Intent(ColorPaletteSettingActivity.this, ColorPaletteActivity.class);
        intent.putExtra("num_color", num_color);
        startActivity(intent);
    }

    public void tapBack(View view) {
        Intent intent = new Intent(ColorPaletteSettingActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
