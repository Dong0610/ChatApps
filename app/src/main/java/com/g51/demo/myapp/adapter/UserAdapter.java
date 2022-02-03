package com.g51.demo.myapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g51.demo.myapp.listener.UserListener;
import com.g51.demo.myapp.databinding.ItemContainerUserBinding;
import com.g51.demo.myapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHoder> implements Filterable {

    private  List<User> users;
    private  List<User> usersold;
    private final UserListener listener;

    public UserAdapter(List<User> users,UserListener listener) {
        this.users = users;
      this.listener=listener;
        this.usersold=users;
    }



    @NonNull
    @Override
    public UserViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding= ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false );
        return new UserViewHoder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull  UserAdapter.UserViewHoder holder, int position) {
        holder.setUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search= constraint.toString();
                if(search.isEmpty()){
                    users= usersold;
                }
                else {
                    List<User> userList= new ArrayList<>();
                    for( User user:usersold){
                        if(user.getName().toLowerCase().contains(search)){
                            userList.add(user);
                        }
                    }
                    users= userList;
                }
                FilterResults filterResults= new FilterResults();
                filterResults.values= users;
                return  filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                users= (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class UserViewHoder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;
        UserViewHoder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding= itemContainerUserBinding;
        }


        void setUserData(User user){
            binding.textname.setText(user.name);
            binding.textEmail.setText(user.email);
            Glide.with(binding.getRoot()).load(user.getImageUri()).into(binding.imageprofile);
            binding.txtStatus.setText(user.getStatus());
            binding.getRoot().setOnClickListener(v->listener.onUserClick(user));
        }
    }
}
