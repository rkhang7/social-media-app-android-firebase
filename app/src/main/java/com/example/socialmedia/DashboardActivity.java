package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FrameLayout content;
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addControls();
        addEvents();
    }

    private void addEvents() {
        // handle bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_user:
                        fragment = new UserFragment();
                        break;
                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();

                return true;
            }
        });
    }

    private void addControls() {
        // profile
         actionBar = getSupportActionBar();
        actionBar.setTitle("Home");

        // init FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //
        content = findViewById(R.id.container);

        //bottom navigation
        bottomNavigationView = findViewById(R.id.navigation);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new HomeFragment())
                .commit();

    }

    private void checkStatusUser(){
        // get current user;
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            // user signed
        }
        else {
            // user do not sign
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkStatusUser();
        super.onStart();
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu when click

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // get menu id
        int id = item.getItemId();
        if(id == R.id.action_signout){
            mAuth.signOut();
            checkStatusUser();
        }
        return super.onOptionsItemSelected(item);
    }
}