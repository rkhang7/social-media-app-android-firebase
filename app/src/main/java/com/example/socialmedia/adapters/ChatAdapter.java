package com.example.socialmedia.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialmedia.R;
import com.example.socialmedia.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private Context mContext;
    private List<Chat> chatList;
    private String hisImage;

    // view chat type
    private static final int MSG_CHAT_LEFT = 1;
    private static final int MSG_CHAT_RIGHT = 2;

    private FirebaseUser user;

    public ChatAdapter(Context mContext, List<Chat> chatList, String hisImage) {
        this.mContext = mContext;
        this.chatList = chatList;
        this.hisImage = hisImage;
    }

    @NonNull

    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = null;

        if(viewType == MSG_CHAT_LEFT){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_left, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_right, parent, false);
        }


        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        // set data
        holder.messageTv.setText(chat.getMessage());



        Timestamp ts = Timestamp.valueOf(chat.getTimestamp());
        Date date = new Date();
        date.setTime(ts.getTime());
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
         holder.timeTv.setText(formattedDate);



        if(!hisImage.equals("")){
            Picasso.get().load(hisImage).into(holder.profileIv);
        }


        if(position == chatList.size() - 1){
            if(chatList.get(position).isSeen()){
                holder.isSeenTv.setText("seen");
            }
            else {
                holder.isSeenTv.setText("delivered");
            }
        }
        else{

            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileIv;
        private TextView messageTv, timeTv, isSeenTv;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        // message of sender
        if(chatList.get(position).getSender().equals(user.getUid())){
            return MSG_CHAT_RIGHT;
        }
        else{
            return MSG_CHAT_LEFT;
        }
    }
}
