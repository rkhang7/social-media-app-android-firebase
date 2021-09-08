package com.example.socialmedia.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.ModalBottomSheet;
import com.example.socialmedia.ProfileFragment;
import com.example.socialmedia.R;
import com.example.socialmedia.ThereProfileActivity;
import com.example.socialmedia.Util;
import com.example.socialmedia.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context mContext;
    private List<Post> postList;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String myId;

    public PostAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    @NonNull

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // set data

        // set avatar
        if (post.getuAvatar().equals("")) {
            holder.avatarIv.setImageResource(R.drawable.ic_face_custom);
        } else {
            Picasso.get().load(post.getuAvatar()).into(holder.avatarIv);
        }

        // name
        holder.nameTv.setText(post.getuName());

        //time
        Timestamp ts = Timestamp.valueOf(post.getpTime());
        Date date = new Date();
        date.setTime(ts.getTime());
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
        holder.timeTv.setText(formattedDate);

        // title
        holder.titleTv.setText(post.getpTitle());

        // description
        if (post.getpDescription().equals("")) {
            holder.descriptionTv.setVisibility(View.GONE);
        } else {
            holder.descriptionTv.setText(post.getpDescription());
        }


        // image
        if (post.getpImage().equals("noImage")) {
            holder.pImageIv.setVisibility(View.GONE);
        } else {
            Picasso.get().load(post.getpImage()).into(holder.pImageIv);
        }


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView nameTv, timeTv, titleTv, descriptionTv, totalLikeTv, totalCommentTv;
        private ImageView pImageIv;
        private Button likeBtn, commentBtn, shareBtn;
        private LinearLayout profileLayout;
        private ImageButton moreBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.u_avatar_iv);
            nameTv = itemView.findViewById(R.id.u_name_tv);
            timeTv = itemView.findViewById(R.id.p_time_tv);
            titleTv = itemView.findViewById(R.id.p_title_tv);
            descriptionTv = itemView.findViewById(R.id.p_description_tv);
            totalLikeTv = itemView.findViewById(R.id.total_like_tv);
            totalCommentTv = itemView.findViewById(R.id.total_comments_tv);
            pImageIv = itemView.findViewById(R.id.p_image_iv);
            likeBtn = itemView.findViewById(R.id.like_btn);
            commentBtn = itemView.findViewById(R.id.comment_btn);
            shareBtn = itemView.findViewById(R.id.share_btn);
            profileLayout = itemView.findViewById(R.id.profile_layout);
            moreBtn = itemView.findViewById(R.id.more_btn);

            // inti firebase
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get my id
                    myId = user.getUid();

                    // get current id;
                    Post post = postList.get(getAdapterPosition());
                    String currentId = post.getUid();

                    if (currentId.equals(myId)) {
                        // start profile fragment
                        AppCompatActivity activity = (AppCompatActivity) mContext;
                        Fragment myFragment = new ProfileFragment();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, myFragment).addToBackStack(null).commit();
                    } else {
                        // start his profile
                        Intent intent = new Intent(mContext, ThereProfileActivity.class);
                        intent.putExtra("uid", currentId);
                        mContext.startActivity(intent);
                    }


                }
            });

            // handle more button clicked
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get my id
                    myId = user.getUid();

                    // get current post
                    Post post = postList.get(getAdapterPosition());


                    if (myId.equals(post.getUid())) {


                        // create modal bottom sheet
                        showBottomSheetDialog(post);

                    }

                }
            });


        }
    }

    private void showBottomSheetDialog(Post post) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.modal_bottom_sheet);


        // init views;
        Button editPostBtn = bottomSheetDialog.findViewById(R.id.edit_post_btn);
        Button deletePostBtn = bottomSheetDialog.findViewById(R.id.delete_post_btn);

        // handle button delete clicked
        deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(post);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void deletePost(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete post");
        builder.setMessage("Do you want delete this post ?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Util.POST_DATABASE).child(post.getpId());
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    private void startHisProfile() {
    }


}
