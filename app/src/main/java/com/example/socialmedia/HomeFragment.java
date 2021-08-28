package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeFragment extends Fragment {

    // Firebase
    private FirebaseAuth mAuth;


    // Views
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // init firebase
        mAuth = FirebaseAuth.getInstance();

        return view;
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