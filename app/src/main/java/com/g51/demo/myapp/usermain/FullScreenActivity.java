package com.g51.demo.myapp.usermain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.g51.demo.myapp.activity.HomeActivity;
import com.g51.demo.myapp.databinding.ActivityFullScreenBinding;

public class FullScreenActivity extends AppCompatActivity {

    private ActivityFullScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        binding.back.setOnClickListener(v->onBackPressed());
        String uri=intent.getStringExtra("uri");
        if(uri!=null){
            Glide.with(binding.getRoot()).load(uri).into(binding.image);
        }
        binding.image.setOnClickListener(v->isback());
    }
    boolean isql=true;
    private void isback(){
        if(isql){
            binding.bottom.setVisibility(View.VISIBLE);
            isql=false;
        }
        else {
            binding.bottom.setVisibility(View.GONE);
            isql=true;
        }
    }
}