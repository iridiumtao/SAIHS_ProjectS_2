package app.jerry960331.saihs_projects_2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private ImageButton
            btnSkStat1, btnSkStat2, btnSkStat3, btnSkStat4,
            btnSkAlarm1, btnSkAlarm2, btnSkAlarm3, btnSkAlarm4,
            btnSkChart1, btnSkChart2, btnSkChart3, btnSkChart4;

    private Switch
            swSk1,
            swSk2,
            swSk3,
            swSk4,
            swBT,
            swWiFi;

    private ProgressDialog progress;

    String BT_comm;
    String WiFi_comm;
    String BTAddress = null;

    static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isBtConnected = false;
    BluetoothAdapter btAdapter = null;
    BluetoothSocket btSocket = null;

    //color
    //may be added to color.xml
    public static int red = 0xfff44336;
    public static int green = 0xff4caf50;
    public static int blue = 0xff2195f3;
    public static int orange = 0xffffc107;

    //snackbar customize
    private Snackbar snackbar;
    private View snackBarView;
    private TextView txVStat, snackBarTxV;


//alt+enter 字串抽離

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title);

        findViews();

        swSk1.setOnClickListener(SwListener);
        swSk2.setOnClickListener(SwListener);
        swSk3.setOnClickListener(SwListener);
        swSk4.setOnClickListener(SwListener);

        swBT.setOnClickListener(SwListener);
        swWiFi.setOnClickListener(SwListener);

        txVStat.bringToFront();

    }

    //ctrl+alt+M
    private void findViews() {
        swSk1 = findViewById(R.id.swSk1);
        swSk2 = findViewById(R.id.swSk2);
        swSk3 = findViewById(R.id.swSk3);
        swSk4 = findViewById(R.id.swSk4);

        swBT = findViewById(R.id.swBT);
        swWiFi = findViewById(R.id.swWiFi);

        btnSkStat1 = findViewById(R.id.btnSkStat1);
        btnSkStat2 = findViewById(R.id.btnSkStat2);
        btnSkStat3 = findViewById(R.id.btnSkStat3);
        btnSkStat4 = findViewById(R.id.btnSkStat4);

        btnSkAlarm1 = findViewById(R.id.btnSkAlarm1);
        btnSkAlarm2 = findViewById(R.id.btnSkAlarm2);
        btnSkAlarm3 = findViewById(R.id.btnSkAlarm3);
        btnSkAlarm4 = findViewById(R.id.btnSkAlarm4);

        btnSkChart1 = findViewById(R.id.btnSkChart1);
        btnSkChart2 = findViewById(R.id.btnSkChart2);
        btnSkChart3 = findViewById(R.id.btnSkChart3);
        btnSkChart4 = findViewById(R.id.btnSkChart4);

        txVStat = findViewById(R.id.txVStat);

    }


    private Switch.OnClickListener SwListener = new Switch.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Switch s = (Switch) v;
            String switchText = s.getText().toString();
            final int switchId = s.getId();
            String switchOnOff;
            if (s.isChecked()) {
                switchOnOff = getResources().getString(R.string.open);
            } else {
                switchOnOff = getResources().getString(R.string.close);
            }
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.confirm)
                    .setMessage(switchOnOff + " " + switchText + "?")
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String SnackbarText;
                            int i = 0;
                            String IO = "";
                            switch (switchId) {
                                case R.id.swSk1:
                                    if (s.isChecked()) {
                                        btnSkStat1.setImageResource(R.drawable.dot_green_48dp);
                                        i = 1;
                                        IO = getResources().getString(R.string.turnOn);
                                        BT_comm = "11B";
                                        WiFi_comm = "11W";

                                    } else {
                                        btnSkStat1.setImageResource(R.drawable.dot_black_48dp);
                                        i = 1;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "10B";
                                        WiFi_comm = "10W";
                                    }
                                    break;
                                case R.id.swSk2:
                                    if (s.isChecked()) {
                                        btnSkStat2.setImageResource(R.drawable.dot_green_48dp);
                                        i = 2;
                                        IO = getResources().getString(R.string.turnOn);
                                        BT_comm = "21B";
                                        WiFi_comm = "21W";
                                    } else {
                                        btnSkStat2.setImageResource(R.drawable.dot_black_48dp);
                                        i = 2;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "20B";
                                        WiFi_comm = "20W";
                                    }
                                    break;
                                case R.id.swSk3:
                                    if (s.isChecked()) {
                                        btnSkStat3.setImageResource(R.drawable.dot_green_48dp);
                                        IO = getResources().getString(R.string.turnOn);
                                        i = 3;
                                        BT_comm = "31B";
                                        WiFi_comm = "31W";
                                    } else {
                                        btnSkStat3.setImageResource(R.drawable.dot_black_48dp);
                                        i = 3;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "30B";
                                        WiFi_comm = "30W";

                                    }
                                    break;
                                case R.id.swSk4:
                                    if (s.isChecked()) {
                                        btnSkStat4.setImageResource(R.drawable.dot_green_48dp);
                                        i = 4;
                                        IO = getResources().getString(R.string.turnOn);
                                        BT_comm = "41B";
                                        WiFi_comm = "41W";
                                    } else {
                                        btnSkStat4.setImageResource(R.drawable.dot_black_48dp);
                                        i = 4;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "40B";
                                        WiFi_comm = "40W";
                                    }
                                    break;
                                case R.id.swBT:
                                    if(s.isChecked()){
                                        new ConnectBT().execute();
                                    }else {

                                    }
                                    break;
                            }
                            CustomizedSnackBar(getResources().getString(R.string.socket) + " " + i + " " + IO);

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (s.isChecked()) {
                                s.setChecked(false);
                            } else {
                                s.setChecked(true);
                            }
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (s.isChecked()) {
                                s.setChecked(false);
                            } else {
                                s.setChecked(true);
                            }
                        }
                    })
                    .show();
        }
    };

    // TODO: 11/6/2018 藍芽點擊開關後 Intent到裝置選擇頁面或自動連線(或許用一個Dialog來處理) 選擇裝置後傳回本頁面。
    //https://github.com/Mayoogh/Arduino-Bluetooth-Basic/blob/master/LED-master/app/src/main/java/com/led_on_off/led/DeviceList.java
    //https://github.com/Mayoogh/Arduino-Bluetooth-Basic/blob/master/LED-master/app/src/main/java/com/led_on_off/led/ledControl.java

    private class ConnectBT extends AsyncTask<Void ,Void ,Void>
    {
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait.");  //show a progress dialog
        }
        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo =btAdapter.getRemoteDevice(BTAddress);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(BTUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                connectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!connectSuccess)
            {
                CustomizedSnackBar("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                CustomizedSnackBar("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }



    public void CustomizedSnackBar(String SnackBarText){
            snackbar = Snackbar.make(findViewById(android.R.id.content), SnackBarText, Snackbar.LENGTH_SHORT)
                    .setAction("DISMISS", null);

            snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(blue);
            snackBarTxV = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);

            snackbar.show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.intent_exit)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentSetting = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intentSetting);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .show();
                break;
            case R.id.action_bt:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.intent_exit)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentBT = new Intent(MainActivity.this, BTActivity.class);
                                startActivity(intentBT);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .show();
                break;
            case R.id.action_notification:
                makeOreoNotification();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void makeOreoNotification() {
        final int NOTIFICATION_ID = 8;
        String channelId = "love";
        String channelName = "我的最愛";
        NotificationManager manager = getNotificationManager(channelId, channelName);

        //產生通知
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_notification_home2)
                        .setContentTitle("安全警示")
                        .setContentText("插座電流狀況異常！請立即前往查看")
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setPriority(2)
                        .setWhen(System.currentTimeMillis())
                        .setChannelId(channelId);  //設定頻道ID
        //送出通知
        manager.notify(1, builder.build());
    }

    @NonNull
    private NotificationManager getNotificationManager(
            String channelId, String channelName) {
        //取得通知管理器
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //產生通知頻道
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH);
            //產生頻道
            manager.createNotificationChannel(channel);
        }
        return manager;
    }

    private void makeNotification() {
        //取得通知管理器
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //產生通知
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_menu_today)
                        .setContentTitle("This is title")
                        .setContentText("Testing")
                        .setContentInfo("This is info")
                        .setWhen(System.currentTimeMillis());
        //送出通知
        manager.notify(1, builder.build());
    }



    /**
     //noinspection SimplifiableIfStatement
     if (id == R.id.action_settings) {
     Intent intent = new Intent(this, SettingsActivity.class);
     startActivity(intent);
     return true;
     }
     */


}


