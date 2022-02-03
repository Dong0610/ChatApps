package com.g51.demo.myapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.g51.demo.myapp.adapter.ChatAdapter;
import com.g51.demo.myapp.databinding.ActivityChatBinding;
import com.g51.demo.myapp.listener.ImageListener;
import com.g51.demo.myapp.model.ChatMessage;
import com.g51.demo.myapp.model.User;
import com.g51.demo.myapp.network.APIClient;
import com.g51.demo.myapp.network.APIService;
import com.g51.demo.myapp.usermain.FullScreenActivity;
import com.g51.demo.myapp.usermain.InfoActivity;
import com.g51.demo.myapp.utility.Contanst;
import com.g51.demo.myapp.utility.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity implements ImageListener {

    private ActivityChatBinding binding;
    private User recevieuser;


    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    private String convesionId = null;
    private Uri uriImg;
    private Boolean isReceiveAvailble = false;
    private StorageReference reference= FirebaseStorage.getInstance().getReference();
    private String uriUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LoadReceiveDetail();
        setLisstener();
        init();
        listeneMess();
        binding.bgSend.setVisibility(View.GONE);
        binding.inputMess.setVisibility(View.VISIBLE);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private String getFileExTension(Uri uri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }



    private void SendNotification(String messbody) {
        APIClient.getClient().create(APIService.class).sendMess(
                Contanst.getRemoteMSGHeader(),
                messbody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject reponeJson = new JSONObject(response.body());
                            JSONArray result = reponeJson.getJSONArray("results");
                            if (reponeJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) result.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast("Notification sent is successfuly");
                } else {
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void ListenerAvaiable() {
        database.collection(Contanst.KEY_COLLECTON_USER)
                .document(recevieuser.id)
                .addSnapshotListener(ChatActivity.this, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        if (value.getLong(Contanst.KEY_AVAIBILITY) != null) {
                            int avaiable = Objects.requireNonNull(
                                    value.getLong(Contanst.KEY_AVAIBILITY)
                            ).intValue();
                            isReceiveAvailble = avaiable == 1;
                        }
                        recevieuser.token = value.getString(Contanst.KEY_FCM_TOKEN);
                        if (recevieuser.image == null) {
                            recevieuser.image = value.getString(Contanst.KEY_IMAGE);
                            chatAdapter.setReceiveProfileImg(getBitMapFromEncoder(recevieuser.image));
                            chatAdapter.notifyItemRangeChanged(0, chatMessages.size());
                        }
                    }
                    if (isReceiveAvailble) {
                        binding.avaible.setVisibility(View.VISIBLE);
                    } else {
                        binding.avaible.setVisibility(View.GONE);
                    }

                });
    }

    private void LoadReceiveDetail() {
        recevieuser = (User) getIntent().getSerializableExtra(Contanst.KEY_USE);
        binding.textname.setText(recevieuser.name);
        binding.imgProfile.setImageBitmap(getBitMapFromEncoder(recevieuser.image));
    }

    private void setLisstener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutsend.setOnClickListener(v -> {
                      sendMess();
        });
        binding.imginfo.setOnClickListener(v-> {Intent it= new Intent(ChatActivity.this, InfoActivity.class);
                                                   startActivity(it);});
        binding.camera.setOnClickListener(v->{
            Intent it= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickIMG.launch(it);

        });
    }

    private final ActivityResultLauncher<Intent> pickIMG= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                        uriImg= result.getData().getData();
                        binding.bgSend.setVisibility(View.VISIBLE);
                        binding.inputMess.setVisibility(View.GONE);
                        binding.imagesend.setImageURI(uriImg);
                    }
                }
            }
    );
    private Bitmap getBitMapFromEncoder(String encoder) {
        if (encoder != null) {
            byte[] bytes = Base64.decode(encoder, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitMapFromEncoder(recevieuser.image),
                preferenceManager.getString(Contanst.KEY_USER_ID), this);
        binding.chatRecycleView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMess() {

        if(uriImg!=null){
            final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExTension(uriImg));
            fileRef.putFile(uriImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String upload;
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, Object> mess = new HashMap<>();
                            mess.put(Contanst.KEY_SENDER_ID, preferenceManager.getString(Contanst.KEY_USER_ID));
                            mess.put(Contanst.KEY_RECEIVE_ID, recevieuser.id);
                            mess.put(Contanst.KEY_MESSAGE,null);
                            mess.put(Contanst.KEY_MESS_IMG,uri.toString());

                            mess.put(Contanst.KEY_TIMETAM, new Date());
                            database.collection(Contanst.KEY_COLECION_CHAT).add(mess);
                            if (convesionId != null) {
                                UpdateConversion(binding.inputMess.getText().toString());
                            } else {
                                HashMap<String, Object> coversion = new HashMap<>();
                                coversion.put(Contanst.KEY_SENDER_ID, preferenceManager.getString(Contanst.KEY_USER_ID));
                                coversion.put(Contanst.KEY_SENDER_NAME, preferenceManager.getString(Contanst.KEY_NAME));
                                coversion.put(Contanst.KEY_SENDER_IMAGE, preferenceManager.getString(Contanst.KEY_IMAGE));
                                coversion.put(Contanst.KEY_RECEIVE_ID, recevieuser.id);
                                coversion.put(Contanst.KEY_RECEIVE_NAME, recevieuser.name);
                                coversion.put(Contanst.KEY_RECEIVE_IMAGE, recevieuser.image);
                                coversion.put(Contanst.KEY_LAST_MESSAGE, binding.inputMess.getText().toString());
                                coversion.put(Contanst.KEY_TIMETAM, new Date());
                                addConversion(coversion);
                            }
                            if (!isReceiveAvailble) {
                                try {
                                    JSONArray token = new JSONArray();
                                    token.put(recevieuser.token);
                                    JSONObject data = new JSONObject();

                                    data.put(Contanst.KEY_USER_ID, preferenceManager.getString(Contanst.KEY_USER_ID));
                                    data.put(Contanst.KEY_NAME, preferenceManager.getString(Contanst.KEY_NAME));
                                    data.put(Contanst.KEY_FCM_TOKEN, preferenceManager.getString(Contanst.KEY_FCM_TOKEN));
                                    data.put(Contanst.KEY_MESSAGE, binding.inputMess.getText().toString());
                                    JSONObject body = new JSONObject();
                                    body.put(Contanst.REMOTE_MSG_DATA, data);
                                    body.put(Contanst.REMOTE_MSG_IDS, token);

                                    SendNotification(body.toString());

                                } catch (Exception e) {
                                    showToast(e.getMessage());
                                }
                            }
                            uriImg=null;
                            binding.inputMess.setVisibility(View.VISIBLE);
                            binding.inputMess.setText(null);


                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull  UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    binding.progressimg.setProgress((int) progress);
                    if(progress==100){
                        binding.bgSend.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    showToast("Upload filed");
                }
            });
        }
        else {
            HashMap<String, Object> mess = new HashMap<>();
            mess.put(Contanst.KEY_SENDER_ID, preferenceManager.getString(Contanst.KEY_USER_ID));
            mess.put(Contanst.KEY_RECEIVE_ID, recevieuser.id);
            mess.put(Contanst.KEY_MESSAGE, binding.inputMess.getText().toString());
            mess.put(Contanst.KEY_MESS_IMG,null);

            mess.put(Contanst.KEY_TIMETAM, new Date());
            database.collection(Contanst.KEY_COLECION_CHAT).add(mess);
            if (convesionId != null) {
                UpdateConversion(binding.inputMess.getText().toString());
            } else {
                HashMap<String, Object> coversion = new HashMap<>();
                coversion.put(Contanst.KEY_SENDER_ID, preferenceManager.getString(Contanst.KEY_USER_ID));
                coversion.put(Contanst.KEY_SENDER_NAME, preferenceManager.getString(Contanst.KEY_NAME));
                coversion.put(Contanst.KEY_SENDER_IMAGE, preferenceManager.getString(Contanst.KEY_IMAGE));
                coversion.put(Contanst.KEY_RECEIVE_ID, recevieuser.id);
                coversion.put(Contanst.KEY_RECEIVE_NAME, recevieuser.name);
                coversion.put(Contanst.KEY_RECEIVE_IMAGE, recevieuser.image);
                coversion.put(Contanst.KEY_LAST_MESSAGE, binding.inputMess.getText().toString());
                coversion.put(Contanst.KEY_TIMETAM, new Date());
                addConversion(coversion);
            }
            if (!isReceiveAvailble) {
                try {
                    JSONArray token = new JSONArray();
                    token.put(recevieuser.token);
                    JSONObject data = new JSONObject();

                    data.put(Contanst.KEY_USER_ID, preferenceManager.getString(Contanst.KEY_USER_ID));
                    data.put(Contanst.KEY_NAME, preferenceManager.getString(Contanst.KEY_NAME));
                    data.put(Contanst.KEY_FCM_TOKEN, preferenceManager.getString(Contanst.KEY_FCM_TOKEN));
                    data.put(Contanst.KEY_MESSAGE, binding.inputMess.getText().toString());
                    JSONObject body = new JSONObject();
                    body.put(Contanst.REMOTE_MSG_DATA, data);
                    body.put(Contanst.REMOTE_MSG_IDS, token);

                    SendNotification(body.toString());

                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
            binding.inputMess.setText(null);

        }

    }

    private String getRealDateTime(Date date) {
        return new SimpleDateFormat("MM-dd-yyyy hh:mm:ss", Locale.getDefault()).format(date);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(Contanst.KEY_SENDER_ID);
                    chatMessage.receviveID = documentChange.getDocument().getString(Contanst.KEY_RECEIVE_ID);
                    chatMessage.mess = documentChange.getDocument().getString(Contanst.KEY_MESSAGE);
                    chatMessage.datetime = getRealDateTime(documentChange.getDocument().getDate(Contanst.KEY_TIMETAM));
                    chatMessage.dateObj = documentChange.getDocument().getDate(Contanst.KEY_TIMETAM);
                    chatMessage.messuri=documentChange.getDocument().getString(Contanst.KEY_MESS_IMG);
                    chatMessages.add(chatMessage);
                }

            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObj.compareTo(obj2.dateObj));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecycleView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecycleView.setVisibility(View.VISIBLE);
        }
        binding.progressbr.setVisibility(View.GONE);
        if (convesionId == null) {
            CheckConversion();
        }
    };

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Contanst.KEY_COLECTION_CONVERSION)
                .add(conversion).addOnSuccessListener(documentReference -> convesionId = documentReference.getId());
    }

    private void UpdateConversion(String message) {
        DocumentReference documentReference;
        documentReference = database.collection(Contanst.KEY_COLECTION_CONVERSION).document(convesionId);
        documentReference.update(
                Contanst.KEY_LAST_MESSAGE,
                message,
                Contanst.KEY_TIMETAM, new Date()
        );
    }

    private void listeneMess() {
        database.collection(Contanst.KEY_COLECION_CHAT)
                .whereEqualTo(Contanst.KEY_SENDER_ID, preferenceManager.getString(Contanst.KEY_USER_ID))
                .whereEqualTo(Contanst.KEY_RECEIVE_ID, recevieuser.id)
                .addSnapshotListener(eventListener);
        database.collection(Contanst.KEY_COLECION_CHAT)
                .whereEqualTo(Contanst.KEY_SENDER_ID, recevieuser.id)
                .whereEqualTo(Contanst.KEY_RECEIVE_ID, preferenceManager.getString(Contanst.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private void CheckConversion() {
        if (chatMessages.size() != 0) {
            CheckConversionRemotely(
                    preferenceManager.getString(Contanst.KEY_USER_ID),
                    recevieuser.id
            );
            CheckConversionRemotely(
                    recevieuser.id,
                    preferenceManager.getString(Contanst.KEY_USER_ID));
        }
    }

    private void CheckConversionRemotely(String senderId, String receiveId) {
        database.collection(Contanst.KEY_COLECTION_CONVERSION)
                .whereEqualTo(Contanst.KEY_SENDER_ID, senderId)
                .whereEqualTo(Contanst.KEY_RECEIVE_ID, receiveId)
                .get()
                .addOnCompleteListener(ConversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> ConversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
            convesionId = snapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        ListenerAvaiable();
    }

    @Override
    public void onImageClick(String uri) {
        Intent intent= new Intent(getApplicationContext(), FullScreenActivity.class);
        intent.putExtra("uri",uri);
        startActivity(intent);
    }

    @Override
    public void onCommentClick(String idpost) {

    }
}





























