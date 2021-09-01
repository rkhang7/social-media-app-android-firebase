package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.HashMap;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {
    private static final int CAMERA_CODE = 100;
    private static final int GALLERY_CODE = 101;
    private ActionBar actionBar;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    // current user info;
    private String myId;
    private String myEmail;
    private String myName;
    private String myAvatar;

    // views
    private EditText titleEt, descriptionEt;
    private ImageView imageView;
    private Button uploadBtn;

    private Uri imageUri = null;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        addControls();
        addEvents();

    }

    private void addEvents() {
        // handle upload post
        String title = titleEt.getText().toString();
        checkTitleIsTemp(title);

        // pick an image from camera or gallery to set imageview
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check permission
                checkPermissionToPickImage();
            }
        });

        String description = descriptionEt.getText().toString();

        uploadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (imageUri == null) {
                    // upload without image
                    uploadWithoutImage(title, description);

                } else {
                    // upload with image

                    uploadPostWithImage(title, String.valueOf(imageUri), description);

                }
            }
        });



    }

    private void uploadWithoutImage(String title, String description) {
        // get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("uid", myId);
        hashMap.put("uName", myName);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uAvatar", myAvatar);
        hashMap.put("pId", timestamp);
        hashMap.put("pTitle", title);
        hashMap.put("pDescription", description);
        hashMap.put("pImage", "noImage");
        hashMap.put("pTime", timestamp);

        firebaseDatabase.getReference(Util.POST_DATABASE).child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // reset view
                        titleEt.setText("");
                        descriptionEt.setText("");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void uploadPostWithImage(String title, String imageStr, String description) {
        // get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String path = "Posts/" + "post_" + timestamp;

        StorageReference reference = FirebaseStorage.getInstance().getReference(path);
        reference.putFile(Uri.parse(imageStr)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imageURL = uri.toString();

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("uid", myId);
                        hashMap.put("uName", myName);
                        hashMap.put("uEmail", myEmail);
                        hashMap.put("uAvatar", myAvatar);
                        hashMap.put("pId", timestamp);
                        hashMap.put("pTitle", title);
                        hashMap.put("pDescription", description);
                        hashMap.put("pImage", imageURL);
                        hashMap.put("pTime", timestamp);

                        firebaseDatabase.getReference(Util.POST_DATABASE).child(timestamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // reset view
                                        titleEt.setText("");
                                        descriptionEt.setText("");
                                        imageView.setImageURI(null);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull  Exception e) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });


                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }




    private void checkPermissionToPickImage() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // if Permission Granted
                showOptionDialog();

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AddPostActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void showOptionDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
        builder.setTitle("Pick image from: ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // pick image from camera
                    pickImageFromCamera();
                } else {
                    // pick image from gallery
                    pickImageFromGallery();
                }
            }
        });

        // create and show dialog
        builder.create().show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void pickImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.TITLE, "Temp Desc");

        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_CODE);
    }

    private void checkTitleIsTemp(String title) {
        uploadBtn.setEnabled(!TextUtils.isEmpty(title));
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                uploadBtn.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uploadBtn.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
                uploadBtn.setEnabled(!TextUtils.isEmpty(s));
            }
        });


    }

    private void addControls() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Add Post");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // get my id
        myId = currentUser.getUid();

        // set subTitle by email
        firebaseDatabase.getReference(Util.USER_DATABASE).child(myId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myEmail = snapshot.child("email").getValue(String.class);
                        actionBar.setSubtitle(myEmail);

                        // get more my info
                        myName = snapshot.child("name").getValue(String.class);
                        myAvatar = snapshot.child("image").getValue(String.class);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // init views
        titleEt = findViewById(R.id.p_title_et);
        descriptionEt = findViewById(R.id.p_description_et);
        imageView = findViewById(R.id.p_image_iv);
        uploadBtn = findViewById(R.id.p_upload_btn);
        progressBar = findViewById(R.id.add_post_pb);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow go to previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkStatusUser();
    }

    private void checkStatusUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // user signed

        } else {
            // user do not sign
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

        // for camera
        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = imageUri;
            imageView.setImageURI(imageUri);

        }

    }
}