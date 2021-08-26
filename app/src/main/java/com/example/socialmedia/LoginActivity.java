package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmailEt, mPasswordEt;
    private Button mLogin;
    private ProgressBar progressBar; // show dialog when registering user
    private FirebaseAuth mAuth;
    private TextView noHaveAccountTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addControls();
        addEvents();
    }

    private void addEvents() {
        // handle login button click
        mLogin.setOnClickListener(v -> {
            // input email, password;
            String email = mEmailEt.getText().toString().trim();
            String password = mPasswordEt.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmailEt.setError("Email invalid");
                mEmailEt.requestFocus();
            }
            else if(password.length() < 6){
                mPasswordEt.setError("Password length at least 6 character");
                mPasswordEt.requestFocus();
            }
            else {
                login(email, password);
            }
        });
    }

    private void login(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            finish();
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addControls() {
        mAuth = FirebaseAuth.getInstance();
        // actionbar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        //enable back home
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mEmailEt = findViewById(R.id.login_email_et);
        mPasswordEt = findViewById(R.id.login_password_et);
        mLogin = findViewById(R.id.login_login_btn);
        progressBar = findViewById(R.id.login_progressbar);
        noHaveAccountTv = findViewById(R.id.no_have_account_tv);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}