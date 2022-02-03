package com.g51.demo.myapp.usermain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.g51.demo.myapp.adapter.PostAdapter;
import com.g51.demo.myapp.databinding.ActivityUserPostBinding;
import com.g51.demo.myapp.listener.ImageListener;
import com.g51.demo.myapp.model.Post;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserPostActivity extends AppCompatActivity implements ImageListener {
    private ActivityUserPostBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private List<Post> postUser;
    private PostAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserPostBinding.inflate(getLayoutInflater());
        preferenceManager= new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        setListener();
        loadUse();
        postUser=getPostInList();
        adapter = new PostAdapter(postUser, this);
        binding.postLoadRecycle.setAdapter(adapter);
    }

    private void loadUse() {
        binding.txtname.setText(preferenceManager.getString(Contanst.KEY_NAME));
        Glide.with(binding.getRoot()).load(preferenceManager.getString(Contanst.KEY_USER_URI)).into(binding.anhdaidien);
    }

    public List<Post> getPostInList() {
        loading(true);
        List<Post> list = new ArrayList<>();
        database.collection(Contanst.KEY_POST_UPLOAD)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Post post = new Post();
                            post.setDaiDien(queryDocumentSnapshot.getString(Contanst.KEY_POST_USER));
                            post.setName(queryDocumentSnapshot.getString(Contanst.KEY_POST_NAME));
                            post.setStaus(queryDocumentSnapshot.getString(Contanst.KEY_POST_STATUS));
                            post.setImgUri(queryDocumentSnapshot.getString(Contanst.KEY_POST_IMAGE));
                            post.setTime(getRealDateTime(queryDocumentSnapshot.getDate(Contanst.KEY_POST_TIME)));
                            post.setDate(queryDocumentSnapshot.getDate(Contanst.KEY_POST_TIME));
                            post.setKeyUserId(queryDocumentSnapshot.getString(Contanst.KEY_USER_ID));
                            post.setIdpost(queryDocumentSnapshot.getId());
                            if(queryDocumentSnapshot.getString(Contanst.KEY_USER_ID).equals(preferenceManager.getString(Contanst.KEY_USER_ID))){
                                list.add(post);
                            }

                        }
                        if(list.size()>0){
                            binding.postLoadRecycle.setVisibility(View.VISIBLE);
                        }
                        Collections.sort(list,(obj1, obj2)->obj2.getDate().compareTo(obj1.getDate()));

                    }
                });
        return list;
    }

    private String getRealDateTime(Date date) {
        return new SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault()).format(date);
    }
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }
    private void setListener() {
    }

    @Override
    public void onImageClick(String uri) {
        Intent intent= new Intent(getApplicationContext(), FullScreenActivity.class);
        intent.putExtra("uri",uri);
        startActivity(intent);
    }

    @Override
    public void onCommentClick(String idpost) {
        Intent intent= new Intent(getApplicationContext(), CommentActivity.class);
        intent.putExtra("idpost",idpost);
        startActivity(intent);
    }
}