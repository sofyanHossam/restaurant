package com.example.resturant;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.resturant.activities.OrderDetailsSellerActivity;
import com.example.resturant.activities.OrderDetailsUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID="MY_NOTIFICATION_CHANNEL_ID";
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
   auth=FirebaseAuth.getInstance();
   user=auth.getCurrentUser();
   String notifyType =remoteMessage.getData().get("notificationType");
        if (notifyType.equals("NewOrder")){
            String buyerId=remoteMessage.getData().get("buyerUid");
            String sellerId=remoteMessage.getData().get("sellerUid");
            String orderId=remoteMessage.getData().get("orderId");
            String notificationTitle=remoteMessage.getData().get("notificationTitle");
            String notificationDescription=remoteMessage.getData().get("notificationDescription");

            if (user!=null && auth.getUid().equals(sellerId))
            {
                showNotification(orderId,sellerId,buyerId,notificationTitle,notificationDescription,notifyType);
            }
        }


        if (notifyType.equals("orderStatuesChanged")){
            String buyerId=remoteMessage.getData().get("buyerUid");
            String sellerId=remoteMessage.getData().get("sellerUid");
            String orderId=remoteMessage.getData().get("orderId");
            String notificationTitle=remoteMessage.getData().get("notificationTitle");
            String notificationDescription=remoteMessage.getData().get("notificationMessaging");
            if (user!=null && auth.getUid().equals(buyerId))
            {
                showNotification(orderId,sellerId,buyerId,notificationTitle,notificationDescription,notifyType);
            }
        }
    }
private void showNotification(String orderId,String sellerId,String buyerId,String notificationTitle,String notificationDescription,String notificationType){
    NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    int notificationId =new Random().nextInt(100000000);
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        setupNotificationChannel(notificationManager);
    }
    Intent intent=null;
    if (notificationType.equals("NewOrder")){
        intent=new Intent(this, OrderDetailsSellerActivity.class);
        intent.putExtra("orderId",orderId);
        intent.putExtra("orderBy",buyerId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }else if (notificationType.equals("orderStatuesChanged")){
        intent=new Intent(this, OrderDetailsUserActivity.class);
        intent.putExtra("orderId",orderId);
        intent.putExtra("orderTo",sellerId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_baseline_category_24);

    Uri notificationSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    NotificationCompat.Builder notificationBuilder =new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
    notificationBuilder.setSmallIcon(R.drawable.ic_baseline_category_24)
            .setLargeIcon(largeIcon)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setSound(notificationSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

    notificationManager.notify(notificationId,notificationBuilder.build());
}
    @RequiresApi(api=Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName="Some Sample Text";
        String channelDescription="Channel Description here";

        NotificationChannel notificationChannel =new NotificationChannel(NOTIFICATION_CHANNEL_ID,channelName,NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setVibrationPattern(new long[]{0,300,0,3000});
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
       if (notificationManager!=null){
           notificationManager.createNotificationChannel(notificationChannel);
       }
    }
}
