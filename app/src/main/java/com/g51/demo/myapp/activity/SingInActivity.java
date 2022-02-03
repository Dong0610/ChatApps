package com.g51.demo.myapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.g51.demo.myapp.databinding.ActivitySinginBinding;
import com.g51.demo.myapp.usermain.AdminActivity;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SingInActivity extends AppCompatActivity {
    private ActivitySinginBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager =new PreferenceManager(getApplicationContext());

        binding= ActivitySinginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
    }
    private void setListener(){
        binding.createnewAccount.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.signin.setOnClickListener(v->{
            if(isValidsSignInDetail()){
                SignIn();
            }
        });
        binding.adimin.setOnClickListener(v->startActivity(new Intent(getApplicationContext(), AdminActivity.class)));

    }
    private void showToast(String mesage){
        Toast.makeText(getApplicationContext(),mesage,Toast.LENGTH_SHORT).show();
    }

    private  void SignIn(){
        Loadding(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Contanst.KEY_COLLECTON_USER)
                .whereEqualTo(Contanst.KEY_EMAIL,binding.emailip.getText().toString())
                .whereEqualTo(Contanst.KEY_PASSWORD,binding.passip.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&& task.getResult()!=null&&
                    task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot= task.getResult().getDocuments().get(0);
                        preferenceManager.putBoollean(Contanst.KEY_IS_SIGN_IN,true);
                        preferenceManager.putString(Contanst.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Contanst.KEY_NAME, documentSnapshot.getString(Contanst.KEY_NAME));
                        preferenceManager.putString(Contanst.KEY_IMAGE,documentSnapshot.getString(Contanst.KEY_IMAGE));
                        preferenceManager.putString(Contanst.KEY_EMAIL,documentSnapshot.getString(Contanst.KEY_EMAIL));
                        preferenceManager.putString(Contanst.KEY_PASSWORD,documentSnapshot.getString(Contanst.KEY_PASSWORD));
                        preferenceManager.putString(Contanst.KEY_USER_URI,documentSnapshot.getString(Contanst.KEY_USER_URI));
                        Intent intent= new Intent(getApplicationContext(),HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        Loadding(false);
                        showToast("Không thể đăng nhập");
                    }
                });
    }
    private  void  Loadding(Boolean iload){
        if(iload){
            binding.signin.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }
        else {
            binding.signin.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private  Boolean isValidsSignInDetail(){
        String pass1 =binding.passip.getText().toString().trim();
        if(binding.emailip.getText().toString().trim().isEmpty()){
            showToast("Hãy nhập email của bạn");
            return false;
        }
        else  if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailip.getText().toString()).matches()){
            showToast("Yêu cầu email");
            return false;
        }
        else if(binding.passip.getText().toString().trim().isEmpty()){
            showToast("Nhập mật khẩu");
            return false;
        }
        else {
            return  true;
        }

    }

}