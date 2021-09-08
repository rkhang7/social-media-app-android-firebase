package com.example.socialmedia;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.adapters.PostAdapter;
import com.example.socialmedia.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {
    private View view;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // views from xml
    private ImageView avatarIv, coverIv;
    private TextView nameTv, emailTv, phoneTv;
    private FloatingActionButton fab;

    // permission constants
    private static final int CAMERA_CODE = 6;
    private static final int GALLERY_CODE = 7;

    // uri
    private Uri avatarUri;
    private Uri coverUri;

    // constant status is avatar or cover
    private String imageStatus;

    // progress
    private String myId;

    // recyclerview
    private RecyclerView recyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        addControls();
        addEvents();

        return view;
    }

    private void addEvents() {
        // handle fab click
        fab.setOnClickListener(v -> {
            handlePermission();
        });
    }

    private void handlePermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // show options to choose
                showEditProfileDialog();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void showEditProfileDialog() {
        // options to show dialog
        String[] options = {"Edit Profile Picture", "Edit Cover Photo", "Edit Name", "Edit Phone"};

        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // set title
        builder.setTitle("Choose Action");
        // set option
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Edit Profile Picture click
                    imageStatus = "avatar";
                    showImageDialog(imageStatus);
                    break;
                case 1:
                    // Edit Cover Photo" click
                    imageStatus = "cover";
                    showImageDialog(imageStatus);
                    break;
                case 2:
                    // Edit Name click
                    showEditNameOrPhoneDialog("name");
                    break;
                case 3:
                    // Edit Phone click
                    showEditNameOrPhoneDialog("phone");
                    break;
            }
        });

        // create and show dialog
        builder.create().show();
    }

    private void showEditNameOrPhoneDialog(String type) {
        // create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // create title
        builder.setTitle("Update " + type);

        // create layout
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(8, 8, 8, 8);

        // create edit text
        EditText dataEt = new EditText(getContext());
        dataEt.setPadding(8, 8, 8, 8);
        dataEt.setHint("Enter data");

        database.getReference("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                String oldData = snapshot.child(type).getValue(String.class);
                dataEt.setText(oldData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // create view
        linearLayout.addView(dataEt);

        //set view
        builder.setView(linearLayout);

        // update data
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newData = dataEt.getText().toString();

                HashMap<String, Object> hashMap = new HashMap();
                hashMap.put(type, newData);

                database.getReference("Users").child(user.getUid())
                        .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {

                    }
                });
                // when update name of user also chang name of post
                if(type.equals("name")){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Util.POST_DATABASE);
                    Query query = databaseReference.orderByChild("uid").equalTo(user.getUid());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            HashMap<String, Object> newNameHashMap = new HashMap<>();
                            newNameHashMap.put("uName", newData);
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                dataSnapshot.getRef().updateChildren(newNameHashMap);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // show dialog
        builder.create().show();

    }

    private void showImageDialog(String imageStatus) {
        // options to show dialog
        String[] options = {"Camera", "Gallery"};

        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // set title
        builder.setTitle("Choose Action");
        // set option
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Camera click
                    openCamera(imageStatus);
                    break;
                case 1:
                    // Gallery click
                    openGallery();
                    break;
            }
        });

        // create and show dialog
        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void openCamera(String imageStatus) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.TITLE, "Temp Desc");
        if (imageStatus.equals("avatar")) {
            avatarUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
            startActivityForResult(intent, CAMERA_CODE);
        } else {
            coverUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, coverUri);
            startActivityForResult(intent, CAMERA_CODE);
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(imageStatus.equals("avatar")){
            if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                avatarUri = data.getData();
                String filePath = "Photo/" + "userId_" + user.getUid() + "_" + "avatar";
                StorageReference reference = FirebaseStorage.getInstance().getReference(filePath);
                // put image to firebase
                reference.putFile(avatarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // get url download
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = uri.toString();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Util.USER_DATABASE).child(user.getUid());


                                HashMap<String, Object> hashMap = new HashMap();
                                hashMap.put("image", imageURL);

                                databaseReference.updateChildren(hashMap);

                                // update avatar of user also update avatar of post
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(Util.POST_DATABASE);
                                Query query = databaseReference1.orderByChild("uid").equalTo(user.getUid());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                        HashMap<String, Object> newAvatarHashMap = new HashMap<>();
                                        newAvatarHashMap.put("uAvatar", imageURL);
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            dataSnapshot.getRef().updateChildren(newAvatarHashMap);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull  DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                });
            }

            // for camera
            if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

                Uri uri = avatarUri;

                String filePath = "Photo/" + "userId_" + user.getUid() + "_" + "avatar";

                StorageReference reference = FirebaseStorage.getInstance().getReference(filePath);
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imageURL = uri.toString();

                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("image", imageURL);
                                reference1.updateChildren(hashMap);


                            }
                        });


                    }
                });


            }
        }
        else{
            if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
                coverUri = data.getData();
                String filePath = "Photo/" + "userId_" + user.getUid() + "_" + "cover";
                StorageReference reference = FirebaseStorage.getInstance().getReference(filePath);
                // put image to firebase
                reference.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // get url download
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = uri.toString();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());


                                HashMap<String, Object> hashMap = new HashMap();
                                hashMap.put("cover", imageURL);

                                databaseReference.updateChildren(hashMap);
                            }
                        });
                    }
                });
            }

            // for camera
            if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

                Uri uri = coverUri;

                String filePath = "Photo/" + "userId_" + user.getUid() + "_" + "avatar";

                StorageReference reference = FirebaseStorage.getInstance().getReference(filePath);
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imageURL = uri.toString();

                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("cover", imageURL);
                                reference1.updateChildren(hashMap);


                            }
                        });


                    }
                });


            }
        }



    }

    private void addControls() {
        // init firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myId = user.getUid();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users").child(user.getUid());
        // init views
        avatarIv = view.findViewById(R.id.avatarIv);
        coverIv = view.findViewById(R.id.coverIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        fab = view.findViewById(R.id.edit_fab);


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
        postAdapter = new PostAdapter(getContext());
        postAdapter.setData(postList);
        recyclerView = view.findViewById(R.id.profile_post_rcv);
        // layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        // set data to recyclerview

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Util.POST_DATABASE);
        Query query = databaseReference.orderByChild("uid").equalTo(myId);
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
    // create menu

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_search).setVisible(false); // hide search view
        menu.findItem(R.id.action_add_post).setVisible(false); // hide add post
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