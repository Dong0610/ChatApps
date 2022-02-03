package com.g51.demo.myapp.usermain;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.g51.demo.myapp.activity.HomeActivity;
import com.g51.demo.myapp.activity.SingInActivity;
import com.g51.demo.myapp.databinding.ActivityUpdateInfoBinding;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class UpdateInfo extends AppCompatActivity {
    private ActivityUpdateInfoBinding binding;
    private PreferenceManager preferenceManager;
    private String encoderimage;
    private FirebaseFirestore database;
    private Uri uriImg;
    private StorageReference reference= FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        database = FirebaseFirestore.getInstance();
        binding.update.setOnClickListener(v->{
            if(uriImg==null&&uriImg==null){
                UpdateNoimg();
            }
            else {
                UpdatewithIMage();
            }
        });



        binding.imgback.setOnClickListener(v -> onBackPressed());
        Glide.with(binding.getRoot()).load(preferenceManager.getString(Contanst.KEY_USER_URI)).into(binding.imgprofile);
        binding.close.setOnClickListener(v->onBackPressed());
        binding.layoutImage.setOnClickListener(v -> {
            Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickIMG.launch(it);
        });

    }

    private  boolean checkPass(){
        String pass= binding.oldpass.getText().toString();
        if(pass.equals(preferenceManager.getString(Contanst.KEY_PASSWORD))){
            return  true;
        }
        else {
            showToat("Sai mật khẩu");
            return  false;
        }

    }


    private String getFileExTension(Uri uri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void UpdatewithIMage() {
        if(checkPass()){
            final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExTension(uriImg));
            fileRef.putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DocumentReference reference= database.collection(Contanst.KEY_COLLECTON_USER)
                                    .document( preferenceManager.getString(Contanst.KEY_USER_ID));
                            reference.update(Contanst.KEY_USER_URI,uri.toString());
                            if(!binding.nename.getText().toString().trim().equals("")){
                                reference.update(Contanst.KEY_NAME,binding.nename.getText().toString());
                            }
                            if(!binding.newpass.getText().toString().equals("")){
                                reference.update(Contanst.KEY_PASSWORD, binding.newpass.getText().toString());
                            }
                            reference.update(Contanst.KEY_IMAGE,encoderimage);
                            if(!binding.status.getText().toString().equals("")){
                                reference.update(Contanst.KEY_USER_STATUS,binding.status.getText().toString());
                            }
                            showToat("Update Success");
                            SingOut();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToat("Upload filed");
                }
            });
        }
        else {
            showToat("Sai mật khẩu");
        }


    }


    private void UpdateNoimg() {
        DocumentReference reference= database.collection(Contanst.KEY_COLLECTON_USER)
                .document( preferenceManager.getString(Contanst.KEY_USER_ID));
        if(!binding.nename.getText().toString().trim().equals("")){
            reference.update(Contanst.KEY_NAME,binding.nename.getText().toString());
        }
        if(!binding.newpass.getText().toString().equals("")){
            reference.update(Contanst.KEY_PASSWORD, binding.newpass.getText().toString());
        }
        if(!binding.status.getText().toString().equals("")){
            reference.update(Contanst.KEY_USER_STATUS,binding.status.getText().toString());
        }
        showToat("Update Success");
        SingOut();

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
    public void showToat(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }


    private String encoderImage(Bitmap bitmap) {
        int preWith = 300;
        int preHeight = bitmap.getHeight() * preWith / bitmap.getWidth();
        Bitmap prevBitMap = Bitmap.createScaledBitmap(bitmap, preWith, preHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        prevBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickIMG = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        uriImg = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uriImg);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imgprofile.setImageBitmap(bitmap);
                            encoderimage = encoderImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}
