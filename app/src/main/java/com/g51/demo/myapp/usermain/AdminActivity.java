package com.g51.demo.myapp.usermain;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.g51.demo.myapp.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imgBack.setOnClickListener(v->onBackPressed());
    }
}