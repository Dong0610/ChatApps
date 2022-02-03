package com.g51.demo.myapp.usermain;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.g51.demo.myapp.activity.BaseActivity;
import com.g51.demo.myapp.activity.SingInActivity;
import com.g51.demo.myapp.adapter.UserAdapter;
import com.g51.demo.myapp.databinding.ActivityInfoBinding;
import com.g51.demo.myapp.databinding.ActivityMainBinding;
import com.g51.demo.myapp.databinding.ActivitySinginBinding;
import com.g51.demo.myapp.model.ChatMessage;
import com.g51.demo.myapp.model.User;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InfoActivity extends BaseActivity {

    private ActivityInfoBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager= new PreferenceManager(getApplicationContext());
        setListener();
        LoatUser();

    }

    private void LoatUser(){
        binding.txtname.setText(preferenceManager.getString(Contanst.KEY_NAME));
        binding.txtEmail.setText(preferenceManager.getString(Contanst.KEY_EMAIL));
        Glide.with(binding.getRoot()).load(preferenceManager.getString(Contanst.KEY_USER_URI)).into(binding.imgprofile);
    }
    private void setListener() {
        binding.daviet.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),UserPostActivity.class)));
        binding.updateinfo.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),UpdateInfo.class)));
        binding.signout.setOnClickListener(v->SingOut());
        binding.imgback.setOnClickListener(v->onBackPressed());
    }
    public void showToat(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }
    private void SingOut(){
        showToat("Sign Out");
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        DocumentReference documentReference= database.collection(Contanst.KEY_COLLECTON_USER)
                .document(preferenceManager.getString(Contanst.KEY_USER_ID));
        HashMap<String,Object> updates= new HashMap<>();
        updates.put(Contanst.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), SingInActivity.class));
            finish();
        }).addOnFailureListener(e->showToat("Can't sign out"));

    }
}
