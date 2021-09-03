package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class HomeFragment extends Fragment {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;


    // Views
    private View view;

    //views
    private List<Post> postList;
    private PostAdapter postAdapter;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);



        addControls();

        return view;
    }

    private void addControls() {
        // init firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext());
        postAdapter.setData(postList);
        recyclerView = view.findViewById(R.id.post_rcv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);



        loadAllPost();
        updateUserInfo();


    }

    private void updateUserInfo() {



    }

    private void loadAllPost() {

        // get current user id
        String myUid = user.getUid();
        firebaseDatabase.getReference(Util.POST_DATABASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);



                }
//                updateUserInfo();
                postAdapter.setData(postList);
                recyclerView.setAdapter(postAdapter);


            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    // create menu

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        menu.findItem(R.id.action_search).setVisible(false); // hide search view
        super.onCreateOptionsMenu(menu, inflater);
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
        else if(id == R.id.action_add_post){
            // start add post activity
            startActivity(new Intent(getActivity(), AddPostActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    private void checkStatusUser(){
        // get current user;
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            // user signed
        }
        else {
            // user do not sign
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}