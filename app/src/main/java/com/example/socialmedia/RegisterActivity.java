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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEmailEt, mPasswordEt;
    private Button mRegisterBtn;
    private ProgressBar progressBar; // show dialog when registering user
    private FirebaseAuth mAuth;
    private TextView haveAccountTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addControls();
        addEvents();


    }

    private void addEvents() {
        mRegisterBtn.setOnClickListener(v -> {
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
                registerUser(email, password);
            }
        });

        // have account click
        haveAccountTv.setOnClickListener(v -> {
            // start login activity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void registerUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // get email and id form auth
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String id = user.getUid();

                            //create hashmap to store user data
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("uid", id);
                            hashMap.put("email", email);
                            hashMap.put("name", "");
                            hashMap.put("phone", "");
                            hashMap.put("image", "");
                            hashMap.put("cover", "");

                            // firebase database instance

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            
                            // path to store user data name "Users"
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(id).setValue(hashMap);




                            progressBar.setVisibility(View.INVISIBLE);

                            // start profile activity
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Register fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addControls() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // actionbar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        //enable back home
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mEmailEt = findViewById(R.id.register_email_et);
        mPasswordEt = findViewById(R.id.register_password_et);
        mRegisterBtn = findViewById(R.id.register_register_btn);
        progressBar = findViewById(R.id.register_progressbar);
        haveAccountTv = findViewById(R.id.have_account_tv);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}