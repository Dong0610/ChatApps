package com.g51.demo.myapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g51.demo.myapp.databinding.ItemContainerRecentConversionBinding;
import com.g51.demo.myapp.listener.ConversionListener;
import com.g51.demo.myapp.model.ChatMessage;
import com.g51.demo.myapp.model.User;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversionViewHoler> {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationAdapter(List<ChatMessage> chatMessages,ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener=conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHoler(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationAdapter.ConversionViewHoler holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHoler extends RecyclerView.ViewHolder {
        ItemContainerRecentConversionBinding binding;
        ConversionViewHoler(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding=itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.imageprofile.setImageBitmap(getConversionImg(chatMessage.conversionImg));
            binding.textname.setText(chatMessage.conversionName);
            if(chatMessage.mess==null||chatMessage.mess.equals("")){
                binding.textRecenMess.setText("Đã gửi một ảnh");
            }
            else {
                binding.textRecenMess.setText(chatMessage.mess);
            }

            binding.getRoot().setOnClickListener(v -> {
                User user= new User();
                user.id= chatMessage.converSionId;
                user.name=chatMessage.conversionName;
                user.image= chatMessage.conversionImg;
                conversionListener.OnConversionClick(user);
            });
        }

    }
    private Bitmap getConversionImg(String encorderImg){
        byte[] bytes= Base64.decode(encorderImg,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
