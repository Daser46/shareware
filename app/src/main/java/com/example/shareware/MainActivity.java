package com.example.shareware;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {
    private boolean session;
    private String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = getIntent().getBooleanExtra("auth",false);
        user = getIntent().getStringExtra("user");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(!session){
                    intent = new Intent(MainActivity.this, App.class);
                }else{
                    intent = new Intent(MainActivity.this, Home.class);
                    intent.putExtra("user", user);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}