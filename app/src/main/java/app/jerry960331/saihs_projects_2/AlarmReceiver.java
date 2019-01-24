package app.jerry960331.saihs_projects_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "鬧鐘響" + MainActivity.active, Toast.LENGTH_LONG).show();

        Log.d("ss", "onReceive: ");// not working


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        try {
            Bundle bundle = intent.getExtras();
            boolean[] socket;
            String purpose;
            socket = bundle.getBooleanArray("socketFromMain");
            purpose = bundle.getString("purposeFromMain");


            Intent intent2 = new Intent("Socket_Action");

            Bundle mainBundle2 = new Bundle();
            mainBundle2.putBooleanArray("socket", socket);
            mainBundle2.putString("purpose", purpose);

            intent2.putExtras(mainBundle2);
            context.sendBroadcast(intent2);

                /*

                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle mainBundle = new Bundle();
                mainBundle.putBooleanArray("socket", socket);
                mainBundle.putString("purpose", purpose);

                mainIntent.putExtras(mainBundle);
                context.startActivity(mainIntent);*/

        } catch (Exception e) {
            Toast.makeText(context, "onReceive\n" + e + "", Toast.LENGTH_LONG).show();
            Log.d("onReceive: ", e + "");
        }


    }


}
