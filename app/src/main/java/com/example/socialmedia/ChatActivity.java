package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.socialmedia.adapters.ChatAdapter;
import com.example.socialmedia.models.Chat;
import com.example.socialmedia.models.User;
import com.example.socialmedia.notifications.ApiService;
import com.example.socialmedia.notifications.Client;
import com.example.socialmedia.notifications.Data;
import com.example.socialmedia.notifications.Response;
import com.example.socialmedia.notifications.Sender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView profileIv;
    private TextView nameTv, userStatusTv;
    private RecyclerView recyclerView;
    private EditText messageEt;
    private ImageButton sendBtn;
    private List<Chat> chatList;
    private ChatAdapter chatAdapter;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;

    // id
    private String myUid;
    private String hisUid;

    // his image;
    private String hisImage;

    // for checking if user has seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    // token
    private String myToken;
    private String hisToken;
    private ApiService apiService;

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
                // get timestamp
                String timestamp = new Timestamp(System.currentTimeMillis()).toString();
                // create chat
                Chat chat = new Chat();
                chat.setSender(myUid);
                chat.setReceiver(hisUid);
                chat.setMessage(message);
                chat.setTimestamp(timestamp);
                chat.setSeen(false);


                // store message to firebase
                firebaseDatabase.getReference("Chats").push().setValue(chat);

                // handle send message notification

                firebaseDatabase.getReference("Users").child(myUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Log.e("TAG", "onDataChange: " + user.getName() );
                        senNotification(hisUid, user.getName(), message);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






                // reset message
                messageEt.setText("");

                // close keyboard
                closeKeyBoard();

            }
        });

        // handle typing status when change edit text
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    updateTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void closeKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void senNotification(String hisUid, String name, String message) {
        // get his token
        firebaseDatabase.getReference("Tokens").child(hisUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hisToken = snapshot.child("token").getValue(String.class);

                Data data = new Data(myUid, "New message",name + ": " + message, hisUid, R.drawable.ic_face_custom);
                Sender sender = new Sender(data, hisToken);

                apiService.postData(sender).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

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

        //init ApiService
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(ApiService.class);

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
                        hisImage = snapshot.child("image").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);
                        String onlineStatus = snapshot.child("onlineStatus").getValue(String.class);
                        String typingStatus = snapshot.child("typingTo").getValue(String.class);
                        // set info
                        nameTv.setText(name);
                        if(!hisImage.equals("")){
                            Picasso.get().load(hisImage).into(profileIv);
                        }

                        // set status
                        if(typingStatus.equals(myUid)){
                            userStatusTv.setText("Typing...");
                        }
                        else {
                            if(onlineStatus.equals("online")){
                                userStatusTv.setText("Online");
                            }
                            else if(onlineStatus.equals("offline")){
                                userStatusTv.setText("Offline");
                            }
                            else{
                                Timestamp ts = Timestamp.valueOf(onlineStatus);
                                Date date = new Date();
                                date.setTime(ts.getTime());
                                String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
                                userStatusTv.setText("Last seen: " + formattedDate);
                            }
                        }

                    }

                    @Override
                    public void onCancelled( DatabaseError error) {

                    }
                });

        // set up show message
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatList = new ArrayList<>();

        ChatAdapter chatAdapter = new ChatAdapter(this,chatList, hisImage);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        readMessage();
        
        seenMessage();

    }

    private void seenMessage() {
        firebaseDatabase.getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(hisUid) && chat.getReceiver().equals(myUid)){
                        HashMap<String, Object> hashMap = new HashMap();
                        hashMap.put("seen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        firebaseDatabase.getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                chatList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(myUid) && chat.getReceiver().equals(hisUid)
                    || chat.getSender().equals(hisUid) && chat.getReceiver().equals(myUid)){
                        chatList.add(chat);
                    }

                }


                chatAdapter = new ChatAdapter(ChatActivity.this,chatList, hisImage);
                chatAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(chatAdapter);


            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

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

    private void updateOnlineStatus(String status){
        DatabaseReference reference = firebaseDatabase.getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        reference.updateChildren(hashMap);
    }

    private void updateTypingStatus(String typing){
        DatabaseReference reference = firebaseDatabase.getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        reference.updateChildren(hashMap);
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
            updateOnlineStatus("offline");
        }
        else{

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        checkStatusUser();
        updateOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        updateOnlineStatus(timestamp);
        updateTypingStatus("noOne");
        super.onPause();
    }
}