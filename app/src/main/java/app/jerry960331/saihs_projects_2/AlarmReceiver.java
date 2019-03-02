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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;

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
        String userUID;
        //assert bundle != null;
        if (bundle.getBooleanArray("socketFromMain") != null && bundle.getString("purposeFromMain") != null) {
            socket = bundle.getBooleanArray("socketFromMain");
            purpose = bundle.getString("purposeFromMain");
            userUID = bundle.getString("userUID");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userUID).child("Blue Storm III").child("command");
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
                        show = context.getString(R.string.open_outlet);
                    } else {
                        show = context.getString(R.string.close_outlet);
                    }

                    if (!isNetworkAvailable(context)) {
                        Notification(context, context.getString(R.string.unable_to_send_outlet_signal), context.getString(R.string.internet_not_connected));
                    } else {
                        Notification(context, context.getString(R.string.outlet_signal_send_successfully), show + showNotify);
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
        }else if (bundle.getString("smoke") != null) {
            Notification(context, context.getString(R.string.warning), context.getString(R.string.bad_gas_detected));
        }

    }

    public void Notification(Context context, String title, String message) {
        //source https://stackoverflow.com/questions/45711925/failed-to-post-notification-on-channel-null-target-api-is-26/50348196

        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String CHANNEL_ID = "alarm";
            CharSequence name = context.getString(R.string.alarm_notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(context.getString(R.string.notify_when_timing_outlets_activated));
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{ 1000, 1000, 1000, 1000, 1000 });
            mChannel.setShowBadge(true);
            mChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm")
                .setSmallIcon(R.drawable.func_air)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                //.setPriority(NotificationManager.IMPORTANCE_HIGH)
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
