package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView profileIv;
    private TextView nameTv, userStatusTv;
    private RecyclerView recyclerView;
    private EditText messageEt;
    private ImageButton sendBtn;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    // id
    private String myUid;
    private String hisUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        addControls();
        addEvents();
    }

    private void addEvents() {
        // handle when click send mess
        sendBtn.setOnClickListener(v -> {
            String message = messageEt.getText().toString().trim();
            if(TextUtils.isEmpty(message)){

            }
            else{
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", myUid);
                hashMap.put("receiver", hisUid);
                hashMap.put("message", message);

                // store message to firebase
                firebaseDatabase.getReference("Chats").setValue(hashMap);

                // reset message
                messageEt.setText("");
            }
        });
    }

    private void addControls() {
        // init views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        recyclerView = findViewById(R.id.chat_rcv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // get hisUid
        hisUid = getIntent().getStringExtra("hisUid");

        // get his info to display
        firebaseDatabase.getReference("Users").child(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot snapshot) {
                        String image = snapshot.child("image").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);

                        // set info
                        nameTv.setText(name);
                        if(!image.equals("")){
                            Picasso.get().load(image).into(profileIv);
                        }
                    }

                    @Override
                    public void onCancelled( DatabaseError error) {

                    }
                });

    }

    private void checkStatusUser(){
        // get current user;
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            // user signed
            myUid = user.getUid();
        }
        else {
            // user do not sign
            startActivity(new Intent(this, MainActivity.class));
           finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_search).setVisible(false); // hide  search view
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_signout){
            mAuth.signOut();
            checkStatusUser();
        }
        else{

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        checkStatusUser();
        super.onStart();
    }
}