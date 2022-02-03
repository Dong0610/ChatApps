package com.g51.demo.myapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g51.demo.myapp.databinding.CommentItemViewBinding;
import com.g51.demo.myapp.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemViewBinding itemViewBinding= CommentItemViewBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false);

        return new CommentViewHolder(itemViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        holder.setCommentData(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        CommentItemViewBinding binding;
        public CommentViewHolder(CommentItemViewBinding commentItemViewBinding) {
            super(commentItemViewBinding.getRoot());
            binding=commentItemViewBinding;
        }

        void setCommentData(Comment comment){
            byte[] bytes = Base64.decode(comment.getUserImg(), Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgProfile.setImageBitmap(bitmap);
            binding.comment.setText(comment.getUsercomment());
            binding.textname.setText(comment.getUsername());
            binding.timecm.setText(comment.getTime());
        }
    }
}
