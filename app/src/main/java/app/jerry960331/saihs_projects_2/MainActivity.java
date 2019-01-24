package app.jerry960331.saihs_projects_2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.ExpandableListActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static android.os.Looper.getMainLooper;


public class MainActivity extends AppCompatActivity {
    Activity c;
    private final static String TAG = "MainActivity";
    static boolean active = false;

    //介面
    private ImageButton
            btnSkStat1, btnSkStat2, btnSkStat3, btnSkStat4,
            btnSkAlarm1, btnSkAlarm2, btnSkAlarm3, btnSkAlarm4,
            btnSkChart1, btnSkChart2, btnSkChart3, btnSkChart4;
    private Switch
            swSk1, swSk2, swSk3, swSk4,
            swConnectionMethod;
    private EditText txSocket1, txSocket2, txSocket3, txSocket4;
    private Button
            btnConnect,
            btnSkAuto1, btnSkAuto2, btnSkAuto3, btnSkAuto4,
            btnLogStart, btnLogStop, btnLogClear;
    private TextView txConnectStat, txLog;
    private ImageView imageConnectStat;

    private ProgressDialog progress;
    private LinearLayout devLayout;
    private boolean logIsOn = false;

    String notificationTitle = "安全警示",
            notificationText = "插座電流狀況異常！請立即前往查看";

    String connectionMethod = "Bluetooth";
    //Bluetooth
    String BT_comm = "";
    String WiFi_comm;
    String BTAddress = null;

    static final UUID btUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter btAdapter = null;
    BluetoothSocket btSocket = null;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int MESSAGE_READ = 2;
    private final static int CONNECTING_STATUS = 3;
    OutputStream btOutputStream;
    InputStream btInputStream;
    static ConnectedThread btConnectedThread;
    Handler btHandler;
    static StringBuilder btDataString = new StringBuilder();

    //選擇、判斷、運算數值
    boolean isBTConnected = false;
    boolean isWiFiConnected = false;
    boolean AutoOn1 = false;
    boolean AutoOn2 = false;
    boolean AutoOn3 = false;
    boolean AutoOn4 = false;
    boolean unsafeCurrent1 = false, unsafeCurrent3 = false;
    boolean devMode = false;
    boolean devModeValue = false;
    boolean AutoTimerIsOn = false;
    boolean AutoTimerRepeatNOPE = false;
    String PIR;
    int safeCurrentValue = 3000;
    String current1 = "0", current2 = "0", current3 = "0", current4 = "0";
    Double currentAv1 = 0.0, currentAv2 = 0.0, currentAv3 = 0.0, currentAv4 = 0.0;
    String chipAutoOn1 = "0", chipAutoOn2 = "0", chipAutoOn3 = "0", chipAutoOn4 = "0";

    //鬧鐘回傳
    boolean isAlarmOn1 = false;
    String alarmSetTime1 = "";
    String alarmSetSchedule1 = "";
    String alarmIntent1 = "";
    ArrayList selectedItems1 = new ArrayList();
    boolean[] checkedItems1 = {false, false, false, false, false, false, false, false, false};
    boolean[] alarmSocketSelect = {false, false, false, false};
    String alarmPurpose = "";


    private long timeCountInMilliSeconds;

    //color
    public static int red = 0xfff44336;
    public static int green = 0xff4caf50;
    public static int blue = 0xff2195f3;
    public static int orange = 0xffffc107;

    //snackBar customize
    private Snackbar snackbar;
    private View snackBarView;
    private TextView txVStat, snackBarTxV;

    TextView TxTest;
    String test = "";
    String sendData = "z";


//alt+enter 字串抽離


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title);

        findViews();
        setOnClickListeners();
        txVStat.bringToFront();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        devLayout.setVisibility(View.GONE);
        //FunctionSetEnable(false);

        //Log.d("RND", Math.random()*180+"");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("command");
        myRef.setValue("Hello, World!");
                //"Hello, World!"


        registerReceiver(broadcastReceiver, new IntentFilter("Socket_Action"));

        int startedFromIntent = 0;
        try {
            Bundle bundle = getIntent().getExtras();
            alarmSocketSelect = bundle.getBooleanArray("socket");
            alarmPurpose = bundle.getString("purpose");
            Toast.makeText(this, "Intent from Broadcast", Toast.LENGTH_SHORT).show();
            Connect(1);
            startedFromIntent = 1;
        } catch (Exception e) {

            Log.d("MainActivity", e + "");
        }


        isAlarmOn1 = getSharedPreferences("alarm1", MODE_PRIVATE).getBoolean("isAlarmOn1", false);
        String schedule1 = getSharedPreferences("alarm1", MODE_PRIVATE).getString("alarmSetSchedule1", "");
        Log.d("onCreate schedule1", schedule1);
        if (schedule1 != "" && schedule1 != null) {
            try {
                for (int i = 0; i < schedule1.length(); i++) {
                    Log.d("looping", schedule1.charAt(i) + "");

                    checkedItems1[Integer.parseInt(schedule1.substring(i, i + 1))] = true;
                    selectedItems1.add(schedule1.substring(i, i + 1));
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "schedule1資料復原錯誤", Toast.LENGTH_LONG).show();
                Log.d("schedule1資料復原錯誤", e + "");
            }

        }
        alarmSetTime1 = getSharedPreferences("alarm1", MODE_PRIVATE).getString("alarmSetTime1", "");


        final int finalStartedFromIntent = startedFromIntent;
        btHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == MESSAGE_READ) {
                    try {
                        String readMessage = new String((byte[]) msg.obj, "UTF-8");
                        btDataString.append(readMessage);
                    } catch (UnsupportedEncodingException uee) {
                        uee.printStackTrace();
                    }

                    int endOfLineIndex = btDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        //tvSB.setText(String.value)
                        if (btDataString.charAt(0) == '#') {
                            try {

                                if (logIsOn) {
                                    txLog.setText(btDataString + "\n" + txLog.getText().toString());
                                }

                                PIR = btDataString.substring(1, 2);//偵測到人會收到0

                                //僅於安全電流範圍收值
                                if (!unsafeCurrent1) {
                                    current1 = btDataString.substring(3, 8);
                                }

                                //若值超過設定電流上限
                                if (Integer.parseInt(btDataString.substring(3, 8)) > safeCurrentValue) {
                                    makeOreoNotification("Warning", "安全警示");
                                    unsafeCurrent1 = true;
                                }
                                current2 = btDataString.substring(9, 14);
                                if (!unsafeCurrent3) {
                                    current3 = btDataString.substring(15, 20);
                                }
                                if (Integer.parseInt(btDataString.substring(15, 20)) > safeCurrentValue) {
                                    makeOreoNotification("Warning", "安全警示");
                                    unsafeCurrent3 = true;
                                }
                                current4 = btDataString.substring(21, 26);

                                chipAutoOn1 = btDataString.substring(27, 28);//自動模式有開
                                chipAutoOn2 = btDataString.substring(29, 30);
                                chipAutoOn3 = btDataString.substring(31, 32);
                                chipAutoOn4 = btDataString.substring(33, 34);


                                if (Integer.parseInt(current1) > safeCurrentValue) {
                                    btnSkStat1.setImageResource(R.drawable.dot_red_48dp);
                                    unsafeCurrent1 = true;
                                }
                                if (Integer.parseInt(current3) > safeCurrentValue) {
                                    btnSkStat3.setImageResource(R.drawable.dot_red_48dp);
                                    unsafeCurrent3 = true;
                                }


                                if (Integer.parseInt(chipAutoOn1) == 1) {
                                    if (Integer.parseInt(PIR) == 0) {
                                        swSk1.setChecked(true);
                                        btnSkStat1.setImageResource(R.drawable.dot_green_48dp);
                                    } else {
                                        swSk1.setChecked(false);
                                        btnSkStat1.setImageResource(R.drawable.dot_black_48dp);
                                    }
                                }
                                if (Integer.parseInt(chipAutoOn2) == 1) {
                                    if (Integer.parseInt(PIR) == 0) {
                                        swSk2.setChecked(true);
                                        btnSkStat2.setImageResource(R.drawable.dot_green_48dp);
                                    } else {
                                        swSk2.setChecked(false);
                                        btnSkStat2.setImageResource(R.drawable.dot_black_48dp);
                                    }
                                }
                                if (Integer.parseInt(chipAutoOn3) == 1) {
                                    if (Integer.parseInt(PIR) == 0) {
                                        swSk3.setChecked(true);
                                        btnSkStat3.setImageResource(R.drawable.dot_green_48dp);
                                    } else {
                                        swSk3.setChecked(false);
                                        btnSkStat3.setImageResource(R.drawable.dot_black_48dp);
                                    }
                                }
                                if (Integer.parseInt(chipAutoOn4) == 1) {
                                    if (Integer.parseInt(PIR) == 0) {
                                        swSk4.setChecked(true);
                                        btnSkStat4.setImageResource(R.drawable.dot_green_48dp);
                                    } else {
                                        swSk4.setChecked(false);
                                        btnSkStat4.setImageResource(R.drawable.dot_black_48dp);
                                    }
                                }


                                if (Integer.parseInt(current1) == 0) {
                                    btnSkStat1.setImageResource(R.drawable.dot_black_48dp);
                                } else if (Integer.parseInt(current1) > 0 && Integer.parseInt(current1) < 3000) {
                                    btnSkStat1.setImageResource(R.drawable.dot_green_48dp);
                                } else if (Integer.parseInt(current1) > 3000) {
                                    btnSkStat1.setImageResource(R.drawable.dot_red_48dp);
                                }
                                if (Integer.parseInt(current2) == 0) {
                                    btnSkStat2.setImageResource(R.drawable.dot_black_48dp);
                                } else if (Integer.parseInt(current2) > 0 && Integer.parseInt(current2) < 3000) {
                                    btnSkStat2.setImageResource(R.drawable.dot_green_48dp);
                                } else if (Integer.parseInt(current2) > 3000) {
                                    btnSkStat2.setImageResource(R.drawable.dot_red_48dp);
                                }
                                if (Integer.parseInt(current3) == 0) {
                                    btnSkStat3.setImageResource(R.drawable.dot_black_48dp);
                                } else if (Integer.parseInt(current3) > 0 && Integer.parseInt(current3) < 3000) {
                                    btnSkStat3.setImageResource(R.drawable.dot_green_48dp);
                                } else if (Integer.parseInt(current3) > 3000) {
                                    btnSkStat3.setImageResource(R.drawable.dot_red_48dp);
                                }
                                if (Integer.parseInt(current4) == 0) {
                                    btnSkStat4.setImageResource(R.drawable.dot_black_48dp);
                                } else if (Integer.parseInt(current4) > 0 && Integer.parseInt(current4) < 3000) {
                                    btnSkStat4.setImageResource(R.drawable.dot_green_48dp);
                                } else if (Integer.parseInt(current4) > 3000) {
                                    btnSkStat4.setImageResource(R.drawable.dot_red_48dp);
                                }

                            } catch (Exception e) {
                                Log.d("e", e + "");
                            }

                        }
                        btDataString.delete(0, btDataString.length());
                    }
                }
                if (msg.what == CONNECTING_STATUS) {
                    try {
                        if (msg.arg1 == 1) {
                            Toast.makeText(getApplicationContext(), R.string.connected_successfully, Toast.LENGTH_SHORT).show();
                        } else {
                            if (finalStartedFromIntent == 1) {
                                Toast.makeText(getApplicationContext(), "連線失敗\n鬧鐘所設定的資料無法傳送至插座", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                            }
                            imageConnectStat.setVisibility(View.VISIBLE);
                            txConnectStat.setText(R.string.failed);
                        }
                    } catch (Exception ignored) {
                    }
                }

            }
        };
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...

            Toast.makeText(MainActivity.this, "Received from alarm successfully", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Received from Alarm");

            try {
                Bundle bundle = getIntent().getExtras();
                if (bundle.getBooleanArray("socket") != null) {
                    alarmSocketSelect = bundle.getBooleanArray("socket");
                }
                alarmPurpose = bundle.getString("purpose");
            }catch (Exception e){
                Toast.makeText(MainActivity.this, "資料傳送失敗\n"+e, Toast.LENGTH_LONG).show();
            }
            autoSocketAction();

        }
    };


    private void autoSocketAction(){

        final int[] j = {0};

        if (!isBTConnected){
            Connect(1);
            return;
        }

        Toast.makeText(MainActivity.this, "正在傳送動作訊號", Toast.LENGTH_LONG).show();

        final Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alarmPurpose.equals("TURN_ON")) {
                    if (alarmSocketSelect[j[0]] && j[0] == 0) {
                        btConnectedThread.write("a");
                    }
                    if (alarmSocketSelect[j[0]] && j[0] == 1) {
                        btConnectedThread.write("c");
                    }
                    if (alarmSocketSelect[j[0]] && j[0] == 2) {
                        btConnectedThread.write("e");
                    }
                    if (alarmSocketSelect[j[0]] && j[0] == 3) {
                        btConnectedThread.write("g");
                    }
                } else if (alarmPurpose.equals("TURN_OFF")) {
                    if (alarmSocketSelect[j[0]] && j[0] == 0) {
                        btConnectedThread.write("b");
                    }
                    if (alarmSocketSelect[j[0]] && j[0] == 1) {
                        btConnectedThread.write("d");
                    }
                    if (alarmSocketSelect[j[0]] && j[0] == 2) {
                        btConnectedThread.write("f");
                    }
                    if (alarmSocketSelect[j[0]] && j[0] == 3) {
                        btConnectedThread.write("h");
                    }
                }
                j[0]++;

                if (j[0] == 5) {
                    handler.removeCallbacksAndMessages(null);
                }
            }

        }, 1000);

    }



    private void setOnClickListeners() {
        swSk1.setOnClickListener(SwListener);
        swSk2.setOnClickListener(SwListener);
        swSk3.setOnClickListener(SwListener);
        swSk4.setOnClickListener(SwListener);


        /*txSocket1.setOnLongClickListener(txChangeListener);
        txSocket2.setOnLongClickListener(txChangeListener);
        txSocket3.setOnLongClickListener(txChangeListener);
        txSocket4.setOnLongClickListener(txChangeListener);*/


        btnSkStat1.setOnClickListener(SkStatListener1);
        btnSkStat2.setOnClickListener(SkStatListener2);
        btnSkStat3.setOnClickListener(SkStatListener3);
        btnSkStat4.setOnClickListener(SkStatListener4);

        btnSkAlarm1.setOnClickListener(SkAlarmListener1);
        btnSkAlarm2.setOnClickListener(SkAlarmListener1);
        btnSkAlarm3.setOnClickListener(SkAlarmListener1);
        btnSkAlarm4.setOnClickListener(SkAlarmListener1);

        btnSkChart1.setOnClickListener(SkChartListener1);
        btnSkChart2.setOnClickListener(SkChartListener2);
        btnSkChart3.setOnClickListener(SkChartListener3);
        btnSkChart4.setOnClickListener(SkChartListener4);

        swConnectionMethod.setOnClickListener(SwConnectionMethodListener);
        btnConnect.setOnClickListener(btnConnectListener);

        btnLogStart.setOnClickListener(LogStart);
        btnLogStop.setOnClickListener(LogStop);
        btnLogClear.setOnClickListener(LogClear);
    }

    //ctrl+alt+M
    private void findViews() {
        swSk1 = findViewById(R.id.swSk1);
        swSk2 = findViewById(R.id.swSk2);
        swSk3 = findViewById(R.id.swSk3);
        swSk4 = findViewById(R.id.swSk4);
        swConnectionMethod = findViewById(R.id.swConnectionMethod);

        txSocket1 = findViewById(R.id.txSocket1);
        txSocket2 = findViewById(R.id.txSocket2);
        txSocket3 = findViewById(R.id.txSocket3);
        txSocket4 = findViewById(R.id.txSocket4);

        btnConnect = findViewById(R.id.btnConnect);
        txConnectStat = findViewById(R.id.txConnectStat);
        imageConnectStat = findViewById(R.id.imageConnectStat);

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

        btnSkAuto1 = findViewById(R.id.btnSkAuto1);
        btnSkAuto2 = findViewById(R.id.btnSkAuto2);
        btnSkAuto3 = findViewById(R.id.btnSkAuto3);
        btnSkAuto4 = findViewById(R.id.btnSkAuto4);

        txVStat = findViewById(R.id.txVStat);
        txLog = findViewById(R.id.txLog);

        devLayout = findViewById(R.id.devLayout);
        btnLogStart = findViewById(R.id.btnLogStart);
        btnLogStop = findViewById(R.id.btnLogStop);
        btnLogClear = findViewById(R.id.btnLogClear);

    }

    //插座開關
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
                            int i = 0;
                            String IO = "";
                            switch (switchId) {
                                case R.id.swSk1:

                                    AutoOn1 = false;
                                    btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto1.setTextColor(getResources().getColor(R.color.colorPrimary));

                                    if (s.isChecked()) {
                                        btnSkStat1.setImageResource(R.drawable.dot_green_48dp);
                                        i = 1;
                                        IO = getResources().getString(R.string.turnOn);
                                        BT_comm = "a";
                                        WiFi_comm = "11W";
                                    } else {
                                        btnSkStat1.setImageResource(R.drawable.dot_black_48dp);
                                        i = 1;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "b";
                                        WiFi_comm = "10W";
                                        unsafeCurrent1 = false;
                                    }
                                    break;
                                case R.id.swSk2:

                                    AutoOn2 = false;
                                    btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto2.setTextColor(getResources().getColor(R.color.colorPrimary));

                                    if (s.isChecked()) {
                                        btnSkStat2.setImageResource(R.drawable.dot_green_48dp);
                                        i = 2;
                                        IO = getResources().getString(R.string.turnOn);
                                        BT_comm = "c";
                                        WiFi_comm = "21W";
                                    } else {
                                        btnSkStat2.setImageResource(R.drawable.dot_black_48dp);
                                        i = 2;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "d";
                                        WiFi_comm = "20W";
                                    }
                                    break;
                                case R.id.swSk3:

                                    AutoOn3 = false;
                                    btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto3.setTextColor(getResources().getColor(R.color.colorPrimary));

                                    if (s.isChecked()) {
                                        btnSkStat3.setImageResource(R.drawable.dot_green_48dp);
                                        IO = getResources().getString(R.string.turnOn);
                                        i = 3;
                                        BT_comm = "e";
                                        WiFi_comm = "31W";
                                    } else {
                                        btnSkStat3.setImageResource(R.drawable.dot_black_48dp);
                                        i = 3;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "f";
                                        WiFi_comm = "30W";
                                        unsafeCurrent3 = false;
                                    }
                                    break;
                                case R.id.swSk4:

                                    AutoOn4 = false;
                                    btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto4.setTextColor(getResources().getColor(R.color.colorPrimary));

                                    if (s.isChecked()) {
                                        btnSkStat4.setImageResource(R.drawable.dot_green_48dp);
                                        i = 4;
                                        IO = getResources().getString(R.string.turnOn);
                                        BT_comm = "g";
                                        WiFi_comm = "41W";
                                    } else {
                                        btnSkStat4.setImageResource(R.drawable.dot_black_48dp);
                                        i = 4;
                                        IO = getResources().getString(R.string.turnOff);
                                        BT_comm = "h";
                                        WiFi_comm = "40W";
                                    }
                                    break;
                            }
                            if (btConnectedThread != null) {
                                String sendData = BT_comm;
                                btConnectedThread.write(sendData);
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

    //目前狀態onClick
    private Button.OnClickListener SkStatListener1 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);

            if (!swSk1.isChecked()) { //沒開
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentStat = getResources().getString(R.string.socket_off);
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.show();
            } else {//有開
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = true;
                //CustomDialog.currentNow = Integer.parseInt(current1);
                CustomDialog.currentAve = currentAv1;
                CustomDialog.show();

                final Handler getCurrentHandler = new Handler(getMainLooper());
                getCurrentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.currentNow = Integer.parseInt(current1);
                        getCurrentHandler.postDelayed(this, 1000);
                    }
                }, 10);

            }


        }
    };
    private Button.OnClickListener SkStatListener2 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if (!swSk2.isChecked()) {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 2;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.show();
            } else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 2;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current2);
                CustomDialog.currentAve = currentAv2;
                CustomDialog.show();
            }
        }
    };
    private Button.OnClickListener SkStatListener3 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if (!swSk3.isChecked()) {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 3;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.show();
            } else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 3;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current3);
                CustomDialog.currentAve = currentAv3;
                CustomDialog.show();
            }
        }
    };
    private Button.OnClickListener SkStatListener4 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if (!swSk4.isChecked()) {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 4;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.show();
            } else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 4;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current4);
                CustomDialog.currentAve = currentAv4;
                CustomDialog.show();

            }

        }
    };

    //鬧鐘OnClick
    private Button.OnClickListener SkAlarmListener1 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            CustomDialog.functionSelect = "Alarm";
            CustomDialog.socketSelect = 1;
            CustomDialog.isAlarmOn1 = isAlarmOn1;
            CustomDialog.alarmSetTime1 = alarmSetTime1;
            CustomDialog.alarmPurpose = alarmPurpose;
            CustomDialog.selectedItems = selectedItems1;
            CustomDialog.alarmSocketSelect = alarmSocketSelect;
            CustomDialog.checkedItems = checkedItems1;

            CustomDialog.show();
            CustomDialog.setAlarmDialogResult(new CustomDialogActivity.OnAlarmDialogResult() {
                public void finish(String result) {

                }

                @Override
                public void isAlarmOn1(Boolean b) {
                    isAlarmOn1 = b;
                }

                @Override
                public void alarmSetTime1(String hhmm) {
                    alarmSetTime1 = hhmm;
                }

                @Override
                public void alarmSetSchedule1(String schedule) {
                    alarmSetSchedule1 = schedule;
                }

                @Override
                public void alarmIntent1(String function) {
                    alarmPurpose = function;
                }

                @Override
                public void alarmSocketSelected(boolean[] alarmSocketSelected) {
                    alarmSocketSelect = alarmSocketSelected;
                }

                @Override
                public void selectedItems(ArrayList selectedItems) {
                    selectedItems1 = selectedItems;
                }

                @Override
                public void checkedItems(boolean[] checkedItems) {
                    checkedItems1 = checkedItems;
                }

                @Override
                public void callStartAlarm(Calendar cal) {
                    startAlarm(cal);
                }

                @Override
                public void callCancelAlarm(Calendar cal) {
                    Log.d("DialogReturnVal", "Alarm canceled ");
                    Toast.makeText(MainActivity.this, "未設置鬧鐘", Toast.LENGTH_SHORT).show();
                    cancelAlarm(cal);
                }
            });
        }
    };

    //表格OnClick
    private Button.OnClickListener SkChartListener1 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            CustomDialog.functionSelect = "Chart2";
            CustomDialog.safeCurrentValue = safeCurrentValue;
            CustomDialog.devModeValue = devModeValue;

            final Handler getCurrentHandler = new Handler(getMainLooper());
            getCurrentHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.current = current1;
                    getCurrentHandler.postDelayed(this, 1000);
                }
            }, 10);

            CustomDialog.setChartDialogResult(new CustomDialogActivity.OnChartDialogResult() {
                @Override
                public void finish(String result) {
                    if (result.equals("Chart2 Finish")) {
                        getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                }
            });

            CustomDialog.show();
        }
    };
    private Button.OnClickListener SkChartListener2 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            CustomDialog.functionSelect = "Chart2";
            CustomDialog.safeCurrentValue = safeCurrentValue;
            CustomDialog.devModeValue = devModeValue;

            final Handler getCurrentHandler = new Handler(getMainLooper());
            getCurrentHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.current = current2;
                    getCurrentHandler.postDelayed(this, 1000);
                }
            }, 10);

            CustomDialog.setChartDialogResult(new CustomDialogActivity.OnChartDialogResult() {
                @Override
                public void finish(String result) {
                    if (result.equals("Chart2 Finish")) {
                        getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                }
            });

            CustomDialog.show();
        }
    };
    private Button.OnClickListener SkChartListener3 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            CustomDialog.functionSelect = "Chart2";
            CustomDialog.safeCurrentValue = safeCurrentValue;
            CustomDialog.devModeValue = devModeValue;

            final Handler getCurrentHandler = new Handler(getMainLooper());
            getCurrentHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.current = current3;
                    getCurrentHandler.postDelayed(this, 1000);
                }
            }, 10);

            CustomDialog.setChartDialogResult(new CustomDialogActivity.OnChartDialogResult() {
                @Override
                public void finish(String result) {
                    if (result.equals("Chart2 Finish")) {
                        getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                }
            });

            CustomDialog.show();
        }
    };
    private Button.OnClickListener SkChartListener4 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            CustomDialog.functionSelect = "Chart2";
            CustomDialog.safeCurrentValue = safeCurrentValue;
            CustomDialog.devModeValue = devModeValue;

            final Handler getCurrentHandler = new Handler(getMainLooper());
            getCurrentHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.current = current4;
                    getCurrentHandler.postDelayed(this, 1000);
                }
            }, 10);

            CustomDialog.setChartDialogResult(new CustomDialogActivity.OnChartDialogResult() {
                @Override
                public void finish(String result) {
                    if (result.equals("Chart2 Finish")) {
                        getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                }
            });

            CustomDialog.show();
        }
    };


    //選擇WiFi、藍牙的連線方式
    private Switch.OnClickListener SwConnectionMethodListener = new Switch.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Switch s = (Switch) v;
            if (!isWiFiConnected && !isBTConnected) {  //若兩者未連線
                if (s.isChecked()) { //BT被選
                    connectionMethod = "Bluetooth";
                    Toast.makeText(getApplicationContext(), R.string.Change_Connection_Method_BT,
                            Toast.LENGTH_SHORT).show();
                } else { //WIFI被選
                    connectionMethod = "Wi-Fi";
                    Toast.makeText(getApplicationContext(), R.string.Change_Connection_Method_WiFI,
                            Toast.LENGTH_SHORT).show();
                }
            } else if (isBTConnected) { //若BT已連線
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.stop_bt_connect_to_wifi)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: 11/8/2018 關閉BT連線，並連接至WIFI
                                //TODO ======================================
                                Toast.makeText(getApplicationContext(), R.string.connecting_with_dots, Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), R.string.connected_successfully, Toast.LENGTH_LONG).show();
                                s.setChecked(true);
                                //TODO ======================================
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                s.setChecked(true);
                                Toast.makeText(getApplicationContext(), R.string.cancelled, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                s.setChecked(true);
                                Toast.makeText(getApplicationContext(), R.string.cancelled, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

            } else if (isWiFiConnected) { //若WIFI已連線 //目前無執行
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.stop_wifi_connect_to_bt)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: 11/8/2018 關閉WIFI連線，並連接至BT
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                s.setChecked(false);
                                Toast.makeText(getApplicationContext(), R.string.cancelled, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                s.setChecked(false);
                                Toast.makeText(getApplicationContext(), R.string.cancelled, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        }
    };

    //幫你打開藍牙
    public void setBluetoothEnable(Boolean enable) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (enable) {
                if (!mBluetoothAdapter.isEnabled()) {
                    //mBluetoothAdapter.enable();
                    Intent enableBtIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//跳出視窗
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                }
            }
        }
    }

    //連線按鈕 OnClick
    private Button.OnClickListener btnConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Connect(0);
        }
    };

    public void Connect(final int i) {
        //btnConnect.setVisibility(View.INVISIBLE);
        //txConnectStat.setVisibility(View.INVISIBLE);
        //imageConnectStat.setVisibility(View.INVISIBLE);

        //if (connectionMethod == "Bluetooth") {

        setBluetoothEnable(true);
        //todo 藍牙裝置選擇界面或自動搜尋
        final String address = "98:D3:33:81:25:60"; //HC05的address
        final String name = "SBLUE";

        if (!btAdapter.isEnabled()) {
            Toast.makeText(getBaseContext(), R.string.please_try_again_after_bt_enable,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (isBTConnected) {

            return;
        }

        //todo progressDialog
        Toast.makeText(getApplicationContext(), R.string.connecting_with_dots, Toast.LENGTH_SHORT).show();
        txConnectStat.setText(R.string.connecting_with_dots);
        imageConnectStat.setVisibility(View.INVISIBLE);

        // Spawn a new thread to avoid blocking the GUI one
        new Thread() {
            public void run() {
                boolean fail = false;
                //取得裝置MAC找到連接的藍芽裝置
                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                try {
                    btSocket = createBluetoothSocket(device);
                    //建立藍芽socket
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket creation failed",
                            Toast.LENGTH_SHORT).show();
                }


                try {
                    btSocket.connect(); //建立藍芽連線
                } catch (IOException e) {
                    try {
                        fail = true;
                        btSocket.close(); //關閉socket
                        //開啟執行緒 顯示訊息
                        btHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket creation failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                if (!fail) {
                    //開啟執行緒用於傳輸及接收資料
                    btConnectedThread = new MainActivity.ConnectedThread(btSocket);
                    btConnectedThread.start();
                    //開啟新執行緒顯示連接裝置名稱
                    btHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();

                    //藍牙連接成功
                    btnConnect.setVisibility(View.INVISIBLE);
                    btConnectedThread.write("z"); //成功後傳值


                    isBTConnected = true;
                    txConnectStat.setVisibility(View.INVISIBLE);
                    //imageConnectStat.setVisibility(View.INVISIBLE);

                    //FunctionSetEnable(true);


                    if (i == 1){
                        autoSocketAction();
                    }

                }
            }
        }.start();
        //} else if (connectionMethod == "Wi-Fi") {
        //    Toast.makeText(getBaseContext(), "Unavailable",
        //            Toast.LENGTH_LONG).show();
        //}
    }

    private void FunctionSetEnable(boolean b) {
        swSk1.setEnabled(b);
        swSk2.setEnabled(b);
        swSk3.setEnabled(b);
        swSk4.setEnabled(b);
        btnSkStat1.setEnabled(b);
        btnSkStat2.setEnabled(b);
        btnSkStat3.setEnabled(b);
        btnSkStat4.setEnabled(b);
        btnSkAlarm1.setEnabled(b);
        btnSkAlarm2.setEnabled(b);
        btnSkAlarm3.setEnabled(b);
        btnSkAlarm4.setEnabled(b);
        btnSkChart1.setEnabled(b);
        btnSkChart2.setEnabled(b);
        btnSkChart3.setEnabled(b);
        btnSkChart4.setEnabled(b);
        btnSkAuto1.setEnabled(b);
        btnSkAuto2.setEnabled(b);
        btnSkAuto3.setEnabled(b);
        btnSkAuto4.setEnabled(b);
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws
            IOException {
        return device.createRfcommSocketToServiceRecord(btUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[2048];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0 && bytes != 1) {
                        Log.d("Read Buffer", bytes + "");
                    }
                    if (bytes != 0 && bytes < 255) {

                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);//todo 連線時會造成crash
                        btHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        void write(String input) {


            byte[] bytes = input.getBytes();
            try {
                Log.d("send data", input);
                mmOutStream.write(bytes);
            } catch (IOException ignored) {
            }
            try {
                Log.d("send data2", input);
                mmOutStream.write(bytes);
            } catch (IOException ignored) {
            }
        }
    }

    private void Disconnect() {

        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (Exception ignored) {
            }
            btSocket = null;
        }

    }

    //auto按鈕的onClickListener
    //因為switch-case有問題 所以用最原始的方法
    //使用單晶自動模式 所以插座鎖定被取消了
    public void auto1(View view) {
        if (!AutoOn1) {
            AutoOn1 = true;
            btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto1.setTextColor(getResources().getColor(R.color.white));
            //swSk1.setEnabled(false);
            AutoTimerRepeatNOPE = false;
            if (btConnectedThread != null) {
                btConnectedThread.write("i");
            }
        } else {
            AutoOn1 = false;
            btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto1.setTextColor(getResources().getColor(R.color.colorPrimary));
            //swSk1.setEnabled(true);
        }
    }

    public void auto2(View view) {
        if (!AutoOn2) {
            AutoOn2 = true;
            btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto2.setTextColor(getResources().getColor(R.color.white));
            //swSk2.setEnabled(false);
            AutoTimerRepeatNOPE = false;
            if (btConnectedThread != null) {
                btConnectedThread.write("j");
            }
        } else {
            AutoOn2 = false;
            btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto2.setTextColor(getResources().getColor(R.color.colorPrimary));
            //swSk2.setEnabled(true);
        }
    }

    public void auto3(View view) {
        if (!AutoOn3) {
            AutoOn3 = true;
            btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto3.setTextColor(getResources().getColor(R.color.white));
            //swSk3.setEnabled(false);
            AutoTimerRepeatNOPE = false;
            if (btConnectedThread != null) {
                btConnectedThread.write("k");
            }
        } else {
            AutoOn3 = false;
            btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto3.setTextColor(getResources().getColor(R.color.colorPrimary));
            //swSk3.setEnabled(true);
        }
    }

    public void auto4(View view) {
        if (!AutoOn4) {
            AutoOn4 = true;
            btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto4.setTextColor(getResources().getColor(R.color.white));
            //swSk4.setEnabled(false);
            AutoTimerRepeatNOPE = false;
            if (btConnectedThread != null) {
                btConnectedThread.write("l");
            }
        } else {
            AutoOn4 = false;
            btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto4.setTextColor(getResources().getColor(R.color.colorPrimary));
            //swSk4.setEnabled(true);
        }
    }

    private void startAlarm(Calendar calendar) {
        Calendar nowCal = Calendar.getInstance(TimeZone.getDefault());


        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }


        //just for test
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 10sec
        calendar.add(Calendar.SECOND, 10);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putBooleanArray("socketFromMain", alarmSocketSelect);
        bundle.putString("purposeFromMain", alarmPurpose);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


        Toast.makeText(this, "下一個鬧鐘已被設在" +
                calendar.get(Calendar.YEAR) + "年" +
                calendar.get(Calendar.MONTH) + 1 + "月" +
                calendar.get(Calendar.DATE) + "日 " +
                calendar.get(Calendar.HOUR) + ":" +
                calendar.get(Calendar.MINUTE) + ":" +
                calendar.get(Calendar.SECOND), Toast.LENGTH_SHORT).show();

        System.out.println("year:" + calendar.get(Calendar.YEAR));
        System.out.println("month:" + calendar.get(Calendar.MONTH));//+1
        System.out.println("week:" + calendar.get(Calendar.DAY_OF_WEEK));//+1
        System.out.println("date:" + calendar.get(Calendar.DATE));
        System.out.println("hour:" + calendar.get(Calendar.HOUR));
        System.out.println("minute:" + calendar.get(Calendar.MINUTE));

    }

    private void cancelAlarm(Calendar cal) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public Button.OnClickListener LogStart = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            logIsOn = true;

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("sw4mA");
            Map<String, Object> data = new HashMap<>();
            data.put("command","Hello, World!");
            myRef.updateChildren(data);

            /*myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()){
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        Log.d(TAG, key+"; Value is: " + value);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });*/

            //this will sound the alarm tone
            //this will sound the alarm once, if you wish to
            //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
            ringtone.play();
        }
    };
    public Button.OnClickListener LogStop = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            logIsOn = false;
        }
    };
    public Button.OnClickListener LogClear = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            txLog.setText("     Log cleared");
        }
    };

    public void CustomizedSnackBar(String SnackBarText) {
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

    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!devMode) {
            menu.findItem(R.id.action_bt).setVisible(false);
            menu.findItem(R.id.action_auto).setVisible(false);
            menu.findItem(R.id.action_notification).setVisible(false);
            menu.findItem(R.id.action_destroy).setVisible(false);
            menu.findItem(R.id.action_log).setVisible(false);
            menu.findItem(R.id.action_devData).setVisible(false);
        } else {
            menu.findItem(R.id.action_bt).setVisible(true);
            menu.findItem(R.id.action_auto).setVisible(true);
            menu.findItem(R.id.action_notification).setVisible(true);
            menu.findItem(R.id.action_destroy).setVisible(true);
            menu.findItem(R.id.action_log).setVisible(true);
            menu.findItem(R.id.action_devData).setVisible(true);


        }
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
                makeOreoNotification("Warning", "安全警示");
                break;
            case R.id.action_auto:
                if (AutoOn1 || AutoOn2 || AutoOn3 || AutoOn4) {
                    //TimeCountDown.start();
                }
                break;
            case R.id.action_dev:
                item.setChecked(!item.isChecked());
                devMode = item.isChecked();
                if (!isWiFiConnected && !isBTConnected) {
                    FunctionSetEnable(item.isChecked());
                }
                break;
            case R.id.action_destroy:
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.action_log:
                item.setChecked(!item.isChecked());
                devLayout.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
                break;
            case R.id.action_devData:
                item.setChecked(!item.isChecked());
                devModeValue = item.isChecked();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    void makeOreoNotification(String channelId, String channelName) {
        final int NOTIFICATION_ID = 8;

        NotificationManager manager = getNotificationManager(channelId, channelName);

        if (channelId.equals("Warning")) {
            //產生通知
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.icon_notification_home2)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationText)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setPriority(2)
                            .setWhen(System.currentTimeMillis())
                            .setChannelId(channelId);  //設定頻道ID
            //送出通知
            manager.notify(1, builder.build());

        } else if (channelId.equals("test")) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.icon_notification_home2)
                            .setContentTitle("Alarm")
                            .setContentText("test")
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setPriority(2)
                            .setWhen(System.currentTimeMillis())
                            .setChannelId(channelId);  //設定頻道ID
            //送出通知
            manager.notify(1, builder.build());
        } else {
            Toast.makeText(this, "NOTIFICATION ERROR", Toast.LENGTH_SHORT).show();
        }

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
                        .setSmallIcon(R.drawable.icon_notification_home2)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationTitle)
                        .setWhen(System.currentTimeMillis());
        //送出通知
        manager.notify(1, builder.build());
    }


    @Override
    protected void onPause() {
        Log.d("MainActivity:", "onPause");
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        Log.d("MainActivity:", "onPostResume");
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity:", "onStart");
        active = true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity:", "onStop");
        active = false;

        String schedule1 = "";
        for (int i = 0; i < selectedItems1.size(); i++) {
            schedule1 += selectedItems1.get(i);
            Log.d("selected1", selectedItems1.indexOf(i) + "");
        }
        Log.d("selectedItems1", schedule1);
        SharedPreferences pref = getSharedPreferences("alarm1", MODE_PRIVATE);
        pref.edit()
                .putBoolean("isAlarmOn1", isAlarmOn1)
                .putString("alarmSetTime1", alarmSetTime1)
                .putString("alarmSetSchedule1", schedule1)
                .apply();
        Toast.makeText(getApplicationContext(), "已將資料儲存至手機", Toast.LENGTH_SHORT).show();
        unregisterReceiver(broadcastReceiver);

        btHandler.removeCallbacksAndMessages(null);
        Disconnect();
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.d("MainActivity:", "onDestroy");
        super.onDestroy();

    }
}


