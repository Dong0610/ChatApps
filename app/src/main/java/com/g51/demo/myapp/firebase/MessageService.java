package com.g51.demo.myapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.g51.demo.myapp.R;
import com.g51.demo.myapp.activity.ChatActivity;
import com.g51.demo.myapp.model.User;
import com.g51.demo.myapp.utility.Contanst;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessageService  extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        User user= new User();
        user.id= remoteMessage.getData().get(Contanst.KEY_USER_ID);
        user.name= remoteMessage.getData().get(Contanst.KEY_NAME);
        user.token= remoteMessage.getData().get(Contanst.KEY_FCM_TOKEN);

        int notifycationId= new Random().nextInt();
        String chanerID= "chat_message";
        Intent intent= new Intent(this, ChatActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra(Contanst.KEY_USE,user);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,chanerID);

        builder.setSmallIcon(R.drawable.ic_notifications);
        builder.setContentTitle(user.name);

        builder.setContentText(remoteMessage.getData().get(Contanst.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get(Contanst.KEY_MESSAGE)
        ));


        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            CharSequence charSequence= "Chat Message";
            String chanelDes= "This notification Chaner is uses for message";
            int important= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel= new NotificationChannel(chanerID,charSequence,important);
            channel.setDescription(chanelDes);
            NotificationManager notificationManager= getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notifycationId,builder.build());


    }
}












