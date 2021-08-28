package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.ChatActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private Context mContext;
    private List<User> userList;

    public UserAdapter(Context mContext, List<User> userList) {
        this.mContext = mContext;
        this.userList = userList;
    }
    public void setData(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        if(user == null){
            return;
        }

        if(user.getImage().equals("")){
            Log.e("TAG", "onBindViewHolder: " + "saas" );
            holder.avatarIv.setImageResource(R.drawable.ic_face_custom);
        }
        else{
            Picasso.get().load(user.getImage()).into(holder.avatarIv);
        }

        holder.emailTv.setText(user.getEmail());
        holder.nameTv.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        if(userList != null){
            return userList.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView nameTv, emailTv;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            emailTv = itemView.findViewById(R.id.emailTv);

            // start chat activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("hisUid", user.getUid());
                    mContext.startActivity(intent);
                }
            });
        }


    }
}
