package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmedia.adapters.PostAdapter;
import com.example.socialmedia.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {
    private ActionBar actionBar;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // views from xml
    private ImageView avatarIv, coverIv;
    private TextView nameTv, emailTv, phoneTv;

    // recyclerview
    private RecyclerView recyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;

    // his id
    private String hisId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        addControls();
    }

    private void addControls() {
        // actionbar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        //enable back home
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // get his id
        hisId = getIntent().getStringExtra("uid");

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users").child(hisId);
        // init views
        avatarIv = findViewById(R.id.avatarIv);
        coverIv = findViewById(R.id.coverIv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);



        readDataUser();

        readDataToRecyclerView();
    }
    private void readDataUser() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);
                String image = snapshot.child("image").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String uid = snapshot.child("uid").getValue(String.class);
                String cover = snapshot.child("cover").getValue(String.class);

                // set data
                emailTv.setText(email);
                nameTv.setText(name);
                phoneTv.setText(phone);
                // avatar
                if (image.equals("")) {
//                    Picasso.get().load(R.drawable.ic_face_black).into(avatarIv);
                    avatarIv.setImageResource(R.drawable.ic_face_custom);
                } else {
                    Picasso.get().load(image).into(avatarIv);
                }
                //cover
                if (cover.equals("")) {
                    Picasso.get().load(R.drawable.ic_face_black).into(coverIv);
                } else {
                    Picasso.get().load(cover).into(coverIv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readDataToRecyclerView() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this);
        postAdapter.setData(postList);
        recyclerView = findViewById(R.id.profile_post_rcv);
        // layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        // set data to recyclerview

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Util.POST_DATABASE);
        Query query = databaseReference.orderByChild("uid").equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter.setData(postList);
                recyclerView.setAdapter(postAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow black previous
        return super.onSupportNavigateUp();
    }
}