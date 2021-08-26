package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mRegisterBtn, mLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        addEvents();



    }

    private void addEvents() {
        mRegisterBtn.setOnClickListener(v -> {
            // start register activity
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    private void addControls() {
        // init views;
        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.login_btn);
    }
}