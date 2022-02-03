package com.g51.demo.myapp.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Contanst.KEY_COLLECTON_USER)
                .document(preferenceManager.getString(Contanst.KEY_USER_ID));


    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Contanst.KEY_AVAIBILITY, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Contanst.KEY_AVAIBILITY,1);
    }
}

















