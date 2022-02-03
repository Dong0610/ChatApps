package com.g51.demo.myapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.g51.demo.myapp.adapter.PostAdapter;
import com.g51.demo.myapp.databinding.ActivityHomeBinding;
import com.g51.demo.myapp.listener.ImageListener;
import com.g51.demo.myapp.model.Post;
import com.g51.demo.myapp.usermain.CommentActivity;
import com.g51.demo.myapp.usermain.FullScreenActivity;
import com.g51.demo.myapp.usermain.InfoActivity;
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

public class HomeActivity extends AppCompatActivity implements ImageListener {
    private ActivityHomeBinding binding;
    private PreferenceManager preferenceManager;
    private  FirebaseFirestore database;
    private List<Post> postInList;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager= new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        setListener();
        LoadUserDtail();
        postInList = getPostInList();
        adapter = new PostAdapter(postInList, this);
        binding.postLoadRecycle.setAdapter(adapter);
    }
    private void LoadUserDtail() {
        binding.txtname.setText(preferenceManager.getString(Contanst.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Contanst.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.anhdaidien.setImageBitmap(bitmap);
    }

    private void setListener() {
        binding.btAddPost.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),PostActivity.class)));
        binding.chatlist.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        binding.fabnewChat.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),UserActivity.class)));
        binding.anhdaidien.setOnClickListener(v->startActivity(new Intent(getApplicationContext(), InfoActivity.class)));
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
                            post.setIdpost(queryDocumentSnapshot.getId());
                            list.add(post);
                        }
                        if(list.size()>0){
                            binding.postLoadRecycle.setVisibility(View.VISIBLE);
                        }

                        Collections.sort(list,(obj1,obj2)->obj2.getDate().compareTo(obj1.getDate()));

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