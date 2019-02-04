package app.jerry960331.saihs_projects_2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlarmReceiver extends BroadcastReceiver {

    String val = "";
    String showNotify = "";
    boolean socketNotify;

    @Override
    public void onReceive(final Context context, Intent intent) {
        //Toast.makeText(context, "定時插座執行", Toast.LENGTH_LONG).show();
        FirebaseApp.initializeApp(context);

        Log.d("ss", "onReceive: ");// not working

        /*Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/

        //try {
        Bundle bundle = intent.getExtras();
        boolean[] socket;
        String purpose;
        assert bundle != null;
        socket = bundle.getBooleanArray("socketFromMain");
        purpose = bundle.getString("purposeFromMain");
        assert purpose != null;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("command");
        if (purpose.equals("TURN_ON")) {

            assert socket != null;
            if (socket[0]) {
                val += "a";
            }
            if (socket[1]) {
                val += "c";
            }
            if (socket[2]) {
                val += "e";
            }
            if (socket[3]) {
                val += "g";
            }

            ref.setValue(val);
        } else {

            assert socket != null;
            if (socket[0]) {
                val += "b";
            }
            if (socket[1]) {
                val += "d";
            }
            if (socket[2]) {
                val += "f";
            }
            if (socket[3]) {
                val += "h";
            }

            ref.setValue(val);
        }


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str = dataSnapshot.getValue().toString();

                for (int i = 0; i < str.length(); i++) {
                    switch (str.substring(i, i + 1)) {
                        case "a":
                            showNotify += "1,";
                            socketNotify = true;
                            break;
                        case "c":
                            showNotify += "2,";
                            socketNotify = true;
                            break;
                        case "e":
                            showNotify += "3,";
                            socketNotify = true;
                            break;
                        case "g":
                            showNotify += "4,";
                            socketNotify = true;
                            break;

                        case "b":
                            showNotify += "1,";
                            socketNotify = false;
                            break;
                        case "d":
                            showNotify += "2,";
                            socketNotify = false;
                            break;
                        case "f":
                            showNotify += "3,";
                            socketNotify = false;
                            break;
                        case "h":
                            showNotify += "4,";
                            socketNotify = false;
                            break;
                    }
                }
                showNotify = showNotify.substring(0, showNotify.length() - 1);
                String show;
                if (socketNotify) {
                    show = "開啟插座：";
                } else {
                    show = "關閉插座：";
                }

                if (!isNetworkAvailable(context)) {
                    Notification(context, "無法傳送插座訊號", "網路未連線");
                } else {
                    Notification(context, "已成功傳送插座訊號", show + showNotify);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*} catch (Exception e) {
            Toast.makeText(context, "onReceive\n" + e + "", Toast.LENGTH_LONG).show();
            Log.d("onReceive: ", e + "");
        }*/
    }

    public void Notification(Context context, String title, String message) {
        //source https://stackoverflow.com/questions/45711925/failed-to-post-notification-on-channel-null-target-api-is-26/50348196

        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String CHANNEL_ID = "alarm";
            CharSequence name = "鬧鐘通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription("定時插座執行時提醒");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{ 1000, 1000, 1000, 1000, 1000 });
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm")
                .setSmallIcon(R.drawable.icon_notification_blue_storm_iii)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setColor(Color.rgb(0, 185, 169));

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    //source https://www.androidbegin.com/tutorial/android-broadcast-receiver-notification-tutorial/
    // Check for network availability
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


}
