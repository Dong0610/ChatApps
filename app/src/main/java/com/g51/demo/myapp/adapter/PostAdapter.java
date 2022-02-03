package com.g51.demo.myapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g51.demo.myapp.databinding.ItemPostViewBinding;
import com.g51.demo.myapp.listener.ImageListener;
import com.g51.demo.myapp.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    public PostAdapter(List<Post> listpost, ImageListener listener) {
        this.listpost = listpost;
        this.listener = listener;
    }

    private List<Post> listpost;
    private final ImageListener listener;

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        ItemPostViewBinding itemPostViewBinding= ItemPostViewBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false);
        return new PostViewHolder(itemPostViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull  PostAdapter.PostViewHolder holder, int position) {
        holder.setPostData(listpost.get(position));
    }

    @Override
    public int getItemCount() {
        return listpost.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder{
        ItemPostViewBinding binding;
        PostViewHolder(ItemPostViewBinding itemPostViewBinding) {
            super(itemPostViewBinding.getRoot());
            binding=itemPostViewBinding;
        }

        void setPostData(Post post){
            binding.imgprofile.setImageBitmap(getUserImage(post.getDaiDien()));
            binding.txtname.setText(post.getName());
            binding.status.setText(post.getStaus());
            binding.txttime.setText(post.getTime());
            Glide.with(binding.getRoot()).load(post.getImgUri()).into(binding.anhdep);
            binding.anhdep.setOnClickListener(v->listener.onImageClick(post.getImgUri()));
            binding.chatlist.setOnClickListener(v->listener.onCommentClick(post.getIdpost()));
        }
        private Bitmap getUserImage(String encoderimage){
            byte [] bytes= Base64.decode(encoderimage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
    }
}
