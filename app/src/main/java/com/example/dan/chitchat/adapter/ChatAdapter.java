package com.example.dan.chitchat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dan.chitchat.Model.Chat;
import com.example.dan.chitchat.R;

import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;

/**
 * Created by DAN on 7/16/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatListViewHolder> {
    private Context mContext;
    private ArrayList<Chat> mChatList;

    public ChatAdapter(Context mContext, ArrayList<Chat> mChatList) {
        this.mContext = mContext;
        this.mChatList = mChatList;
    }


    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.item_chat,null);
        return new ChatListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {


        final Chat message = mChatList.get(position);
        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            holder.messageTextView.setVisibility(View.GONE);
            holder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(holder.photoImageView);
        } else {
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.messageTextView.setText(message.getMessages());
        }
        holder.authorTextView.setText(message.getName());
    }


    public void addChat(Chat chat){
        mChatList.add(chat);
        notifyItemInserted(mChatList.size());
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    class ChatListViewHolder extends RecyclerView.ViewHolder{
        ImageView photoImageView;
        TextView messageTextView;
        TextView authorTextView;

        public ChatListViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.photoImageView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            authorTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        }
    }
}
