package com.g51.demo.myapp.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g51.demo.myapp.databinding.ItemContainerRecevieMessBinding;
import com.g51.demo.myapp.databinding.ItemContainerSendMessageBinding;
import com.g51.demo.myapp.listener.ImageListener;
import com.g51.demo.myapp.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  Bitmap receiveProfileImg;
    private  final List<ChatMessage> chatMessages;
    private final String senderID;
    final ImageListener listener;


    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiveProfileImg, String senderID, ImageListener listener) {
        this.receiveProfileImg = receiveProfileImg;
        this.chatMessages = chatMessages;
        this.senderID = senderID;
        this.listener = listener;
    }

    public static final int VIEW_TYPE_SEND=1;
    public static final int VIEW_TYPE_RECEIVE=2;

    public  void setReceiveProfileImg(Bitmap bitmap){
        receiveProfileImg= bitmap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SEND){
            return new SendMessViewHolder(
                    ItemContainerSendMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,false
                    ), listener);
        }
        else {
            return new ReceiveMessViewHolder(
                    ItemContainerRecevieMessBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,false
            ), listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SEND){
            ((SendMessViewHolder)holder).setData(chatMessages.get(position));
        }
        else {
            ((ReceiveMessViewHolder)holder).setData(
                    chatMessages.get(position),receiveProfileImg);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
       if(chatMessages.get(position).senderID.equals(senderID)){
           return VIEW_TYPE_SEND;
       }
       else {
           return VIEW_TYPE_RECEIVE;
       }
    }

    static class SendMessViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSendMessageBinding binding;
        private final ImageListener listener;
        SendMessViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding, ImageListener listener){
            super(itemContainerSendMessageBinding.getRoot());
            binding= itemContainerSendMessageBinding;
            this.listener = listener;
        }
        void setData(ChatMessage chatMessage){
            if(chatMessage.mess==null){

                binding.yesimg.setVisibility(View.VISIBLE);
                binding.noimg.setVisibility(View.GONE);
            }
            else {
               binding.yesimg.setVisibility(View.GONE);
               binding.noimg.setVisibility(View.VISIBLE);
            }
            binding.textSend.setText(chatMessage.mess);
            Glide.with(binding.getRoot()).load(chatMessage.messuri).into(binding.anhsend);
            binding.texttime.setText(chatMessage.datetime);
            binding.texttimeno.setText(chatMessage.datetime);
            binding.anhsend.setOnClickListener(v->listener.onImageClick(chatMessage.messuri));

        }
    }

    static class ReceiveMessViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerRecevieMessBinding binding;
        private final ImageListener listener;
        ReceiveMessViewHolder(ItemContainerRecevieMessBinding itemContainerRecevieMessBinding, ImageListener listener){
            super(itemContainerRecevieMessBinding.getRoot());
            binding=itemContainerRecevieMessBinding;
            this.listener = listener;
        }
        void setData(ChatMessage chatMessage, Bitmap receiveImg){
            if(chatMessage.mess==null){
                binding.yesimg.setVisibility(View.VISIBLE);
                binding.noimg.setVisibility(View.GONE);
            }
            else {
                binding.yesimg.setVisibility(View.GONE);
                binding.noimg.setVisibility(View.VISIBLE);
            }
            binding.textReceive.setText(chatMessage.mess);
            binding.texttimeimg.setText(chatMessage.datetime);
            binding.texttimere.setText(chatMessage.datetime);
            Glide.with(binding.getRoot()).load(chatMessage.messuri).into(binding.anhreceive);
            binding.anhreceive.setOnClickListener(v->listener.onImageClick(chatMessage.messuri));
            if (receiveImg!=null){
                binding.imgprofile.setImageBitmap(receiveImg);
                binding.imgprofileimg.setImageBitmap(receiveImg);
            }
        }
    }
}








