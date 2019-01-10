package app.jerry960331.saihs_projects_2;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "鬧鐘響", Toast.LENGTH_LONG).show();


        Log.d("alarm", "onReceive: ");
    }


}
