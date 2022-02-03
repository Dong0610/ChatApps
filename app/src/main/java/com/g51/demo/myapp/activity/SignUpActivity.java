package com.g51.demo.myapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.g51.demo.myapp.databinding.ActivitySignUpBinding;
import com.g51.demo.myapp.model.MailAccount;
import com.g51.demo.myapp.model.User;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private  String encoderimage;
    FirebaseFirestore database ;
    private PreferenceManager preferenceManager;
    private Uri uriImg;

    private StorageReference reference= FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database= FirebaseFirestore.getInstance();
        preferenceManager =new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBollean(Contanst.KEY_IS_SIGN_IN)){
            Intent it= new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(it);
            finish();
        }
        setListener();

    }
    private  void setListener(){
        binding.signintext.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),SingInActivity.class)));
        binding.signup.setOnClickListener(v->{ if(isValidsSignUpDetail()){ signup(uriImg);}});
        binding.layoutImage.setOnClickListener( v->{
            Intent it= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickIMG.launch(it);
        });
    }
    private void showToast(String mesage){
        Toast.makeText(getApplicationContext(),mesage,Toast.LENGTH_SHORT).show();
    }
    private  void signup(Uri uriImg){
        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExTension(uriImg));
            fileRef.putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseFirestore database= FirebaseFirestore.getInstance();
                            HashMap<String,Object> user= new HashMap<>();
                            user.put(Contanst.KEY_NAME,binding.nameip.getText().toString());
                            user.put(Contanst.KEY_EMAIL,binding.emailip.getText().toString());
                            user.put(Contanst.KEY_PASSWORD,binding.passip.getText().toString());
                            user.put(Contanst.KEY_IMAGE, encoderimage);
                            user.put(Contanst.KEY_USER_URI,uri.toString());
                            user.put(Contanst.KEY_USER_STATUS,"");
                            database.collection(Contanst.KEY_COLLECTON_USER).add(user).addOnSuccessListener(
                                    documentReference -> {
                                        preferenceManager.putBoollean(Contanst.KEY_IS_SIGN_IN,true);
                                        preferenceManager.putString(Contanst.KEY_USER_ID,documentReference.getId());
                                        preferenceManager.putString(Contanst.KEY_NAME,binding.nameip.getText().toString());
                                        preferenceManager.putString(Contanst.KEY_IMAGE,encoderimage);
                                        preferenceManager.putString(Contanst.KEY_EMAIL,binding.emailip.getText().toString());
                                        preferenceManager.putString(Contanst.KEY_PASSWORD,binding.passip.getText().toString());
                                        preferenceManager.putString(Contanst.KEY_USER_URI,uri.toString());
                                        Intent it= new Intent(getApplicationContext(),HomeActivity.class);
                                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(it);
                                    }
                            ).addOnFailureListener(
                                    e -> {
                                        showToast(e.getMessage());
                                    }
                            );
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    showToast("Không thể  upload");
                }
        });
    }
    private List<User> getUserInList() {
        List<User> list = new ArrayList<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Contanst.KEY_COLLECTON_USER)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            User user = new User();
                            user.setName(queryDocumentSnapshot.getString(Contanst.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(Contanst.KEY_EMAIL));
                            user.setImage(queryDocumentSnapshot.getString(Contanst.KEY_IMAGE));
                            user.setToken(queryDocumentSnapshot.getString(Contanst.KEY_FCM_TOKEN));
                            user.setImageUri(queryDocumentSnapshot.getString(Contanst.KEY_USER_URI));
                            user.setStatus(queryDocumentSnapshot.getString(Contanst.KEY_USER_STATUS));
                            user.id = queryDocumentSnapshot.getId();
                            list.add(user);
                        }
                    }
                });

        return list;
    }


    private  String encoderImage(Bitmap bitmap){
        int preWith=100;
        int preHeight=bitmap.getHeight()*preWith/bitmap.getWidth();
        Bitmap prevBitMap = Bitmap.createScaledBitmap(bitmap,preWith,preHeight,false);
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        prevBitMap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes= byteArrayOutputStream.toByteArray();
        return  Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private String getFileExTension(Uri uri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private final ActivityResultLauncher<Intent> pickIMG= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                         uriImg= result.getData().getData();
                        try{
                            InputStream inputStream= getContentResolver().openInputStream(uriImg);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imgprofile.setImageBitmap(bitmap);
                            binding.textAddimg.setVisibility(View.GONE);
                            encoderimage= encoderImage(bitmap);
                        }
                        catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private  int checkEmail(){
        int a=0;
        List<User> accounts= getUserInList();
        for (User ac: accounts){
            if(binding.emailip.getText().toString().equals(ac.getEmail())){
                a=1;
            }
        }
        return a;
    }
    private  Boolean isValidsSignUpDetail(){
        String pass1 =binding.passip.getText().toString().trim();
        String pass2=binding.passconfirmip.getText().toString().trim();
        if(encoderimage==null){
            showToast("Hãy chọn 1 ảnh đại diện");
            return  false;
        }
        else if(binding.nameip.getText().toString().trim().isEmpty()){
            showToast("Nhập tên");
            return false;
        }
        else  if(binding.emailip.getText().toString().trim().isEmpty()){
            showToast("Nhập email");
            return false;
        }
        else if(checkEmail()==1){
            showToast("Email đã được sử dụng");
            return  false;
        }
        else  if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailip.getText().toString()).matches()){
            showToast("Yêu cầu 1 email");
            return false;
        }
        else if(binding.passip.getText().toString().trim().isEmpty()){
            showToast("Hãy nhập mật khẩu");
            return false;
        }
        else if(!pass1.equals(pass2)){
            showToast("Mật khẩu không khớp");
            return  false;
        }
        else{
      return  true;}
    }
}













