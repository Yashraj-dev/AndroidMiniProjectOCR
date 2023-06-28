package com.example.opticalcharacterrecognitionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  {
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart=findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(i);
                }
        );
    }
}