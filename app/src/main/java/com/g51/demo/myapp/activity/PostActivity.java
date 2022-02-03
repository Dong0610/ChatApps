package com.g51.demo.myapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.g51.demo.myapp.databinding.ActivityPostBinding;
import com.g51.demo.myapp.listener.ImageListener;
import com.g51.demo.myapp.model.Post;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    private StorageReference reference= FirebaseStorage.getInstance().getReference();
    private Uri uriImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database= FirebaseFirestore.getInstance();
        preferenceManager= new PreferenceManager(getApplicationContext());
        setListener();
        loadUserDetail();
    }

    private void loadUserDetail() {
        binding.txtname.setText(preferenceManager.getString(Contanst.KEY_NAME));
        byte [] bytes= Base64.decode(preferenceManager.getString(Contanst.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imgProfile.setImageBitmap(bitmap);
    }

    private void setListener() {
        binding.choseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChoose();
            }
        });

        binding.btUploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    uploadtoFirebase(uriImg);
            }
        });
      binding.btHome.setOnClickListener(v->onBackPressed());
    }

    private void openFileChoose() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,2);
    }

    private void uploadtoFirebase(Uri uri) {
        Date date= new Date();
        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExTension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.progressload.setProgress(0);
                            }
                        }, 500);
                        HashMap<String,Object> post= new HashMap<>();
                        post.put(Contanst.KEY_USER_ID,preferenceManager.getString(Contanst.KEY_USER_ID));
                        post.put(Contanst.KEY_POST_USER,preferenceManager.getString(Contanst.KEY_IMAGE));
                        post.put(Contanst.KEY_POST_NAME,preferenceManager.getString(Contanst.KEY_NAME));
                        post.put(Contanst.KEY_POST_STATUS,binding.txtStatus.getText().toString());
                        post.put(Contanst.KEY_POST_IMAGE,uri.toString());
                        post.put(Contanst.KEY_POST_TIME,new Date());
                        database.collection(Contanst.KEY_POST_UPLOAD).add(post).addOnSuccessListener(
                                documentReference -> {
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                }
                        );
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull  UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                binding.progressload.setProgress((int) progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                showToast("không thể upload");
            }
        });
    }

    private String getFileExTension(Uri uri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private  void showToast(String mess){
        Toast.makeText(this,mess,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==2 && resultCode == RESULT_OK && data != null){
            uriImg = data.getData();
            binding.choseImg.setImageURI(uriImg);
        }

    }

}