package com.wasidnp.firebase;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.wasidnp.R;
import com.wasidnp.activities.Helper.SessionManager;
import com.wasidnp.activities.MainActivity;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "Material Wallpaper";
    NotificationCompat.Builder notificationBuilder;
    SessionManager mSessionManager ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    //It is optional
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

//Calling method to generate notification
        boolean notificationEnable = mSessionManager.getNotEnable ();
        boolean notificationEnableDaily = mSessionManager.getNotDailyEnable ();
        if(notificationEnable == true ) {
            sendNotification ( remoteMessage.getNotification ( ).getTitle ( ), remoteMessage.getNotification ( ).getBody ( ) );
        }else {
            Log.d ( "","Notification disabled from settings" );
        } if(notificationEnableDaily == true ) {
            sendNotification ( remoteMessage.getNotification ( ).getTitle ( ), remoteMessage.getNotification ( ).getBody ( ) );
        }else {
            Log.d ( "","Notification daily disabled from settings" );
        }

// weekly notification enable/disable
        if(mSessionManager.setNotWeeklyEnable ( true )){
            showNotificationWeekly(getApplication ());
        }

      }

    private void showNotificationWeekly(Application application) {
        Intent intent = new Intent(getApplicationContext (), FirebaseMessagingService.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext (), 2020, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext ())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("WEEKLY Hot Actesses Wallpaper Notification")
                .setContentText("Check out the weekly new hot actress wallpapers for your devices");
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults( Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getApplication ().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2020, mBuilder.build());
    }


    //This method is only generating push notification
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

// sound_enable_disble for notifaiction
    if(mSessionManager.getNotSoundEnable () == true) {
         Uri defaultSoundUri = RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_NOTIFICATION );
         notificationBuilder = new NotificationCompat.Builder ( this )
                 .setSmallIcon ( R.mipmap.ic_launcher )
                 .setContentTitle ( title )
                 .setContentText ( messageBody )
                 .setAutoCancel ( true )
                 .setSound ( defaultSoundUri )
                 .setContentIntent ( pendingIntent );
     }else if(mSessionManager.getNotSoundEnable () == false){
         Uri defaultSoundUri = RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_NOTIFICATION );
         notificationBuilder = new NotificationCompat.Builder ( this )
                 .setSmallIcon ( R.mipmap.ic_launcher )
                 .setContentTitle ( title )
                 .setContentText ( messageBody )
                 .setAutoCancel ( true )
                 .setSound ( defaultSoundUri )
                 .setContentIntent ( pendingIntent );

     }
// vibration_enable_disable
        if(mSessionManager.getNotVibEnable () == true) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_NOTIFICATION );
            notificationBuilder = new NotificationCompat.Builder ( this )
                    .setSmallIcon ( R.mipmap.ic_launcher )
                    .setContentTitle ( title )
                    .setContentText ( messageBody )
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setAutoCancel ( true )
                    .setSound ( defaultSoundUri )
                    .setContentIntent ( pendingIntent );
        }else if(mSessionManager.getNotVibEnable () == false){
            Uri defaultSoundUri = RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_NOTIFICATION );
            notificationBuilder = new NotificationCompat.Builder ( this )
                    .setSmallIcon ( R.mipmap.ic_launcher )
                    .setContentTitle ( title )
                    .setContentText ( messageBody )
                    .setAutoCancel ( true )
                    .setSound ( defaultSoundUri )
                    .setContentIntent ( pendingIntent );
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0, notificationBuilder.build());
    }

}