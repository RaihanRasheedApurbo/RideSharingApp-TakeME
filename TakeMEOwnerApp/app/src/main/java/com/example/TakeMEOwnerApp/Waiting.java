package com.example.TakeMEOwnerApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

public class Waiting extends AppCompatActivity {

    public static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, SPLASH_TIME_OUT);




    }

}