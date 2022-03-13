package com.example.netane;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PuzzleSettingActivity extends AppCompatActivity {

    EditText editRowSize, editColSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_setting);

        editColSize = findViewById(R.id.edit_colSize);
        editRowSize = findViewById(R.id.edit_rowSize);
    }

    public void tapStart(View view) {
        try {
            Intent intent = new Intent(PuzzleSettingActivity.this, PuzzleActivity.class);
            int rowSize = Integer.parseInt(editRowSize.getText().toString());
            int colSize = Integer.parseInt(editColSize.getText().toString());
            if ((rowSize < 2) || (colSize < 2)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PuzzleSettingActivity.this);
                builder.setMessage(R.string.dialog_writePositive).show();
                return;
            }
            intent.putExtra("col", colSize);
            intent.putExtra("row", rowSize);
            startActivity(intent);
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PuzzleSettingActivity.this);
            builder.setMessage(R.string.dialog_writeNumber).show();
        }
    }

    public void tapBack(View view) {
        Intent intent = new Intent(PuzzleSettingActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
