package com.g51.demo.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.g51.demo.myapp.adapter.RecentConversationAdapter;
import com.g51.demo.myapp.databinding.ActivityMainBinding;
import com.g51.demo.myapp.listener.ConversionListener;
import com.g51.demo.myapp.model.ChatMessage;
import com.g51.demo.myapp.model.User;
import com.g51.demo.myapp.usermain.UpdateInfo;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversion;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;

    private void Init() {
        conversion = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversion, this);
        binding.conversationRecycleView.setAdapter(conversationAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        getToken();
        LoadUserDtail();
        setListener();
        LoadChat();
    }

    void LoadChat() {
        Init();
        listenerConversation();
    }

    private void listenerConversation() {
        database.collection(Contanst.KEY_COLECTION_CONVERSION)
                .whereEqualTo(Contanst.KEY_SENDER_ID, preferenceManager.getString(Contanst.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Contanst.KEY_COLECTION_CONVERSION)
                .whereEqualTo(Contanst.KEY_RECEIVE_ID, preferenceManager.getString(Contanst.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Contanst.KEY_SENDER_ID);
                    String receveId = documentChange.getDocument().getString(Contanst.KEY_RECEIVE_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = senderId;
                    chatMessage.receviveID = receveId;

                    if (preferenceManager.getString(Contanst.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImg = documentChange.getDocument().getString(Contanst.KEY_RECEIVE_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Contanst.KEY_RECEIVE_NAME);
                        chatMessage.converSionId = documentChange.getDocument().getString(Contanst.KEY_RECEIVE_ID);
                    } else {
                        chatMessage.conversionImg = documentChange.getDocument().getString(Contanst.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Contanst.KEY_SENDER_NAME);
                        chatMessage.converSionId = documentChange.getDocument().getString(Contanst.KEY_SENDER_ID);
                    }
                    chatMessage.mess = documentChange.getDocument().getString(Contanst.KEY_LAST_MESSAGE);
                    chatMessage.dateObj = documentChange.getDocument().getDate(Contanst.KEY_TIMETAM);
                    conversion.add(chatMessage);

                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {

                    for (int i = 0; i < conversion.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Contanst.KEY_SENDER_ID);
                        String reiveId = documentChange.getDocument().getString(Contanst.KEY_RECEIVE_ID);
                        if (conversion.get(i).senderID.equals(senderId) && conversion.get(i).receviveID.equals(reiveId)) {
                            conversion.get(i).mess = documentChange.getDocument().getString(Contanst.KEY_LAST_MESSAGE);
                            conversion.get(i).dateObj = documentChange.getDocument().getDate(Contanst.KEY_TIMETAM);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversion, (obj1, obj2) -> obj2.dateObj.compareTo(obj1.dateObj));
            conversationAdapter.notifyDataSetChanged();
            binding.conversationRecycleView.smoothScrollToPosition(0);
            binding.conversationRecycleView.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.GONE);

        }
    };

    private void setListener() {
        binding.fabnewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UserActivity.class)));
        binding.btHome.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(),HomeActivity.class)));
        binding.chatlist.setOnClickListener(v -> LoadChat());
    }

    private void LoadUserDtail() {
        binding.txtname.setText(preferenceManager.getString(Contanst.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Contanst.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imgProfile.setImageBitmap(bitmap);
    }

    public void showToat(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Contanst.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentSnapshot = database.collection(Contanst.KEY_COLLECTON_USER)
                .document(preferenceManager.getString(Contanst.KEY_USER_ID));
        documentSnapshot.update(Contanst.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> {
                    showToat("Unable update token");
                });
    }

    @Override
    public void OnConversionClick(User user) {
        Intent it = new Intent(getApplicationContext(), ChatActivity.class);
        it.putExtra(Contanst.KEY_USE, user);
        startActivity(it);
    }




}














