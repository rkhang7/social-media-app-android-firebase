package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.ThereProfileActivity;
import com.example.socialmedia.models.Post;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private Context mContext;
    private List<Post> postList;

    public PostAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Post> postList){
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
        if(post.getuAvatar().equals("")){
            holder.avatarIv.setImageResource(R.drawable.ic_face_custom);
        }
        else {
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
        if(post.getpDescription().equals("")){
            holder.descriptionTv.setVisibility(View.GONE);
        }
        else {
            holder.descriptionTv.setText(post.getpDescription());
        }


        // image
        if(post.getpImage().equals("noImage")){
            holder.pImageIv.setVisibility(View.GONE);
        }
        else{
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

            profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ThereProfileActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
