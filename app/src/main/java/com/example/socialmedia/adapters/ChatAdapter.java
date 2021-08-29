package com.example.socialmedia.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.socialmedia.R;
import com.example.socialmedia.models.Chat;
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
import java.util.Date;
import java.util.HashMap;
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
         TextView messageTv, timeTv, isSeenTv;
        private LinearLayout messageLayout;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);

            // handle delete message
            messageLayout.setOnClickListener(v -> {
                deleteMessage(getAdapterPosition());
            });
        }
    }


    private void deleteMessage(int position) {
        String myUid = user.getUid();

        // get current message
        Chat chat = chatList.get(position);

        // only delete sender's message
        if(chat.getSender().equals(myUid)){
            // create confirm dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Delete");
            builder.setMessage("Are you sure to delete this message? ");

            // handle button
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String msg = "This message was deleted";

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

                    // <=> message == chat.getMessage() --> log ra la biet
                    databaseReference.orderByChild("message").equalTo(chat.getMessage()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("message", msg);

                                // magic
                                dataSnapshot.getRef().updateChildren(hashMap);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // show dialog
            builder.create().show();
        }
        else {
            // nothing
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
