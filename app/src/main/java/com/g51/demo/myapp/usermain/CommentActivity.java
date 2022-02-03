package com.g51.demo.myapp.usermain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.g51.demo.myapp.adapter.CommentAdapter;
import com.g51.demo.myapp.databinding.ActivityCommentBinding;
import com.g51.demo.myapp.model.Comment;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentBinding binding;
    private FirebaseFirestore database= FirebaseFirestore.getInstance();
    private PreferenceManager preferenceManager;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCommentBinding.inflate(getLayoutInflater());
        preferenceManager= new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        CommentInit();
        setListener();
    }

    public void CommentInit(){
        List<Comment> comments= loadComment();
        adapter= new CommentAdapter(comments);
        binding.chatRecycleView.setAdapter(adapter);
    }

    private  String keypost(){
        Intent intent=getIntent();
        String idpost=intent.getStringExtra("idpost");
        return  idpost;
    }
    private void setListener() {

        binding.layoutsend.setOnClickListener(v->Comment());
        binding.imageBack.setOnClickListener(v->onBackPressed());
    }

    private void Comment() {
        HashMap<String,Object> comment= new HashMap<>();
        comment.put(Contanst.KEY_COMMENT_POSTID,keypost());
        comment.put(Contanst.KEY_COMMENT_IMG,preferenceManager.getString(Contanst.KEY_IMAGE));
        comment.put(Contanst.KEY_COMMENT_NAME,preferenceManager.getString(Contanst.KEY_NAME));
        comment.put(Contanst.KEY_COMMENT_MES,binding.inputMess.getText().toString());
        comment.put(Contanst.KEY_COMMENT_TIME,new Date());
        database.collection(Contanst.KEY_COMMENT).add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                showToast("Successsfull");
                binding.inputMess.setText(null);
                CommentInit();
            }
        });
    }
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressbr.setVisibility(View.VISIBLE);
        } else {
            binding.progressbr.setVisibility(View.INVISIBLE);
        }
    }
    private String getRealDateTime(Date date) {
        return new SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault()).format(date);
    }

    private  void showToast(String mess){
        Toast.makeText(this,mess,Toast.LENGTH_LONG).show();
    }

    public List<Comment> loadComment() {
        loading(true);
        List<Comment> comments = new ArrayList<>();
        database.collection(Contanst.KEY_COMMENT).
                get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Comment comment= new Comment();
                            comment.setUserImg(queryDocumentSnapshot.getString(Contanst.KEY_COMMENT_IMG));
                            comment.setUsername(queryDocumentSnapshot.getString(Contanst.KEY_COMMENT_NAME));
                            comment.setUsercomment(queryDocumentSnapshot.getString(Contanst.KEY_COMMENT_MES));
                            comment.setTime(getRealDateTime(queryDocumentSnapshot.getDate(Contanst.KEY_COMMENT_TIME)));
                            comment.setPostid(queryDocumentSnapshot.getString(Contanst.KEY_COMMENT_POSTID));
                            comment.setDate(queryDocumentSnapshot.getDate(Contanst.KEY_COMMENT_TIME));
                            if(queryDocumentSnapshot.getString(Contanst.KEY_COMMENT_POSTID).equals(keypost())){
                                comments.add(comment);
                            }
                        }

                        if(comments.size()>0){
                            binding.txtall.setText("Tất cả các bình luận");
                            binding.chatRecycleView.setVisibility(View.VISIBLE);
                        }
                        else{
                            binding.txtall.setText("Chưa có bình luận");
                        }

                        Collections.sort(comments,(c1,c2)->c1.getDate().compareTo(c2.getDate()));

                    }
                });

        return comments;
    }

}














