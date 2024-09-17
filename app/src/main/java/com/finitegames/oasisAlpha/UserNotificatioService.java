package com.finitegames.oasisAlpha;

import static android.os.Build.*;
import static android.os.Build.VERSION.*;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class UserNotificatioService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: http7s://goo.gl/39bRNJ
        Log.d("From: ",remoteMessage.getFrom());

        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            System.out.println("Message data payload: " + remoteMessage.getData());
//        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            System.out.println("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String url = remoteMessage.getData().get("url");
        int id = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("id")));


        //sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        sendNotification(title, message, url, id);
    }

    //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


    private void sendNotification(String Title,String message,String url,int id) {
        Intent intent = new Intent(this, BaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //String ursi = "https://finitegames.co.za/office/fleets";

        intent.putExtra("URL", url);
        System.out.println("Message URL : " + url);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int flagMutable;

        if(SDK_INT >= VERSION_CODES.S){
            flagMutable = PendingIntent.FLAG_MUTABLE;
        }else{
            flagMutable = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        @SuppressLint({"UnspecifiedImmutableFlag", "InlinedApi"})
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id , intent, flagMutable);

        String channelId = "fcm_default_channel";

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(Title)
                        .setSmallIcon(R.drawable.oasis)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.oasis))
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSound(defaultSound)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (SDK_INT >= VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }
}
