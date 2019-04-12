package app.jerry960331.saihs_projects_2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import io.fabric.sdk.android.Fabric;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends AppCompatActivity {
    Activity c;
    private final static String TAG = "MainActivity";
    static boolean active = false;
    String StrR = "";
    private Menu menu;

    //介面
    private ImageButton
            btnSkStat1, btnSkStat2, btnSkStat3, btnSkStat4,
            btnSkAlarm, btnSkInfo,
            btnSkChart1, btnSkChart2, btnSkChart3, btnSkChart4;
    private Switch
            swSk1, swSk2, swSk3, swSk4;
    private Button
            btnConnect,
            btnSkAuto1, btnSkAuto2, btnSkAuto3, btnSkAuto4,
            btnLogStart, btnLogStop, btnLogClear;
    private TextView txConnectStat, txLog;

    private LinearLayout devLayout;
    private boolean logIsOn = false;
    private ImageView imgNetworkStat, imgBTStat;
    ProgressDialog progressDialog;

    String notificationTitle,
            notificationText;

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
    boolean AutoOn1 = false, AutoOn2 = false, AutoOn3 = false, AutoOn4 = false;
    boolean AutoHW1 = false, AutoHW2 = false, AutoHW3 = false, AutoHW4 = false;
    boolean unsafeCurrent1 = false, unsafeCurrent2 = false, unsafeCurrent3 = false, unsafeCurrent4 = false;
    boolean devMode = false;
    boolean devModeValue = false;
    boolean AutoTimerIsOn = false;
    boolean AutoTimerRepeatNOPE = false;
    String PIR;
    int safeCurrentValue;
    String current1 = "0", current2 = "0", current3 = "0", current4 = "0";
    Double currentAv1 = 0.0, currentAv2 = 0.0, currentAv3 = 0.0, currentAv4 = 0.0;
    int socketStat1 = 0, socketStat2 = 0, socketStat3 = 0, socketStat4 = 0;
    Handler getCurrentHandler;

    //Firebase
    FirebaseDatabase firebase;
    DatabaseReference dbRef;

    //鬧鐘回傳
    boolean isAlarmOn1 = false;
    String alarmSetTime1 = "";
    String alarmSetSchedule1 = "";
    String alarmIntent1 = "";
    ArrayList selectedItems1 = new ArrayList();
    boolean[] checkedItems1 = {false, false, false, false, false, false, false, false, false};
    boolean[] alarmSocketSelect = {false, false, false, false};
    String alarmPurpose = "";

    //color
    public static int red = 0xfff44336;
    public static int green = 0xff4caf50;
    public static int blue = 0xff2195f3;
    public static int orange = 0xffffc107;

    //snackBar customize
    Snackbar snackbar;
    View snackBarView;
    private TextView txVStat, snackBarTxV;

    TextView TxTest;
    String test = "";
    String sendData = "z";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private String userUID;
    boolean statOnCloud = false;

    private String userEmail;
    private String userName = "";
    private String userDevice;
    private String appTitle;
    boolean showFSA;
    private String language;
    private String country;

//alt+enter 字串抽離


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        language = getSharedPreferences("user", MODE_PRIVATE).getString("language", Locale.getDefault().getLanguage());
        country = getSharedPreferences("user", MODE_PRIVATE).getString("country", Locale.getDefault().getCountry());
        Locale locale = new Locale(language,country);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        Log.d(TAG, "onCreate: "+language+" "+country);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViews();
        setOnClickListeners();
        txVStat.bringToFront();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        devLayout.setVisibility(View.GONE);
        notificationTitle = getResources().getString(R.string.Security_warning);
        notificationText = getResources().getString(R.string.socket_current_warning);
        onCreateFirebaseCheck();


        onCreateGetSharedPreference();
        btHandler();
        showCase();
    }

    private void onCreateGetSharedPreference() {
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
                Toast.makeText(getApplicationContext(), "schedule1" + getResources().getString(R.string.data_load_fail), Toast.LENGTH_LONG).show();
                Log.d("schedule1資料復原錯誤", e + "");
            }
        }
        alarmSetTime1 = getSharedPreferences("alarm1", MODE_PRIVATE).getString("alarmSetTime1", "");
        userEmail = getSharedPreferences("user", MODE_PRIVATE).getString("user_email", null);
        userName = getSharedPreferences("user", MODE_PRIVATE).getString("user_name", null);
        userDevice = getSharedPreferences("user", MODE_PRIVATE).getString("user_device_BS", "Blue Storm III");
        showFSA = getSharedPreferences("user", MODE_PRIVATE).getBoolean("showFSA", true);
        swSk1.setText(getSharedPreferences("user", MODE_PRIVATE).getString("socket1", getResources().getString(R.string.socket_1)));
        swSk2.setText(getSharedPreferences("user", MODE_PRIVATE).getString("socket2", getResources().getString(R.string.socket_2)));
        swSk3.setText(getSharedPreferences("user", MODE_PRIVATE).getString("socket3", getResources().getString(R.string.socket_3)));
        swSk4.setText(getSharedPreferences("user", MODE_PRIVATE).getString("socket4", getResources().getString(R.string.socket_4)));
        appTitle = getSharedPreferences("user", MODE_PRIVATE).getString("appTitle", getString(R.string.title));
        Log.d(TAG, appTitle + "apple  " + R.string.title);
        setTitle(appTitle);
        safeCurrentValue = getSharedPreferences("user", MODE_PRIVATE).getInt("safeCurrentValue", 5000);
    }

    private void showCase(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "function");

        sequence.setConfig(config);

        int color = 0xd200b9a9;

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnConnect)
                        .setContentText(R.string.btConnect_desc)
                        .setDismissText(R.string.got_it)
                        .setMaskColour(color)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(swSk1)
                        .setSkipText(R.string.skip)
                        .setContentText(R.string.sw_desc)
                        .setDismissText(R.string.got_it)
                        .setMaskColour(color)
                        .withOvalShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnSkStat1)
                        .setSkipText(R.string.skip)
                        .setDismissText(R.string.got_it)
                        .setContentText(R.string.stat_desc)
                        .setMaskColour(color)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnSkChart1)
                        .setDismissText(R.string.got_it)
                        .setContentText(R.string.chart_desc)
                        .setMaskColour(color)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnSkAuto1)
                        .setDismissText(R.string.got_it)
                        .setContentText(R.string.auto_desc)
                        .setMaskColour(color)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnSkAlarm)
                        .setDismissText(R.string.got_it)
                        .setContentText(R.string.alarm_desc)
                        .setMaskColour(color)
                        .build()
        );

        sequence.start();

    }

    @SuppressLint("HandlerLeak")
    private void btHandler() {
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
                    mainCtrlLoop(btDataString);
                }
                if (msg.what == CONNECTING_STATUS) {
                    progressDialog.dismiss();
                    try {
                        if (msg.arg1 == 1) {
                            Toast.makeText(getApplicationContext(), R.string.connected_successfully, Toast.LENGTH_SHORT).show();
                            imgBTStat.setImageResource(R.drawable.dot_green);
                            btnConnect.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                            imgBTStat.setImageResource(R.drawable.dot_gray);

                        }
                    } catch (Exception ignored) {
                    }
                }

            }
        };
    }

    private void mainCtrlLoop(StringBuilder btDataString) {
        int endOfLineIndex = btDataString.indexOf("~");
        if (endOfLineIndex > 0) {
            //000000000011111111112222222222333333
            //012345678901234567890123456789012345
            //#0+00000+00000+00000+00000+0+0+0+0~
            //#0+00000+00000+00000+00000+0+0+0+0+0+0+0+0~
            //tvSB.setText(String.value)
            if (btDataString.charAt(0) == '#') {
                try {
                    btDataString.setLength(35);

                    if (logIsOn) {
                        txLog.setText(btDataString + "\n" + txLog.getText().toString());
                    }
                    PIR = btDataString.substring(1, 2);//偵測到人會收到0

                    //僅於安全電流範圍收值
                    //todo 暫時以轉換為Integer的方式讓錯誤的封包自動被截掉，未來需讓current改為Int
                    if (!unsafeCurrent1) {
                        current1 = Integer.parseInt(btDataString.substring(3, 8)) + "";
                    }
                    if (!unsafeCurrent2) {
                        current2 = Integer.parseInt(btDataString.substring(9, 14)) + "";
                    }
                    if (!unsafeCurrent3) {
                        current3 = Integer.parseInt(btDataString.substring(15, 20)) + "";
                    }
                    if (!unsafeCurrent4) {
                        current4 = Integer.parseInt(btDataString.substring(21, 26)) + "";
                    }

                    boolean statChanged1 = false, statChanged2 = false, statChanged3 = false, statChanged4 = false;
                    if (socketStat1 != Integer.parseInt(btDataString.substring(27, 28)) && !AutoHW1) {
                        statChanged1 = true;
                        if (socketStat1 == 5 && Integer.parseInt(btDataString.substring(27, 28)) != 5) {
                            swSk1.setEnabled(true);
                        }
                    }
                    if (socketStat2 != Integer.parseInt(btDataString.substring(29, 30)) && !AutoHW2) {
                        statChanged2 = true;
                        if (socketStat2 == 5 && Integer.parseInt(btDataString.substring(29, 30)) != 5) {
                            swSk2.setEnabled(true);
                        }
                    }
                    if (socketStat3 != Integer.parseInt(btDataString.substring(31, 32)) && !AutoHW3) {
                        statChanged3 = true;
                        if (socketStat3 == 5 && Integer.parseInt(btDataString.substring(31, 32)) != 5) {
                            swSk3.setEnabled(true);
                        }
                    }
                    if (socketStat4 != Integer.parseInt(btDataString.substring(33, 34)) && !AutoHW4) {
                        statChanged4 = true;
                        if (socketStat4 == 5 && Integer.parseInt(btDataString.substring(33, 234)) != 5) {
                            swSk4.setEnabled(true);
                        }
                    }

                    //插座狀態
                    socketStat1 = Integer.parseInt(btDataString.substring(27, 28));
                    socketStat2 = Integer.parseInt(btDataString.substring(29, 30));
                    socketStat3 = Integer.parseInt(btDataString.substring(31, 32));
                    socketStat4 = Integer.parseInt(btDataString.substring(33, 34));

                    // 1 黑燈 OFF
                    // 2 綠燈 ON
                    // 3 藍燈 OFF
                    // 4 藍燈 ON
                    // 5 紅燈 OFF 35
                    // 00000000011111111112222222222333333
                    //012345678901234567890123456789012345
                    //#0+00000+00000+00000+00000+0+0+0+0~
                    switch (socketStat1) {
                        case 1:
                            btnSkStat1.setImageResource(R.drawable.dot_black);
                            swSk1.setChecked(false);
                            SkAuto1(false);
                            if (statChanged1)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk1.getText() + getString(R.string.has_been_turn_off), blue);
                            break;
                        case 2:
                            btnSkStat1.setImageResource(R.drawable.dot_green);
                            swSk1.setChecked(true);
                            SkAuto1(false);
                            if (statChanged1)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk1.getText() + getString(R.string.has_been_turn_on), blue);
                            break;
                        case 3:
                            btnSkStat1.setImageResource(R.drawable.dot_blue);
                            swSk1.setChecked(false);
                            SkAuto1(true);
                            if (statChanged1)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk1.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 4:
                            btnSkStat1.setImageResource(R.drawable.dot_blue);
                            swSk1.setChecked(true);
                            SkAuto1(true);
                            if (statChanged1)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk1.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 5:
                            btnSkStat1.setImageResource(R.drawable.dot_red);
                            swSk1.setChecked(false);
                            swSk1.setEnabled(false);
                            if (statChanged1) {
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk1.getText() + getString(R.string.abnormal_current), red);
                                makeOreoNotification("Warning", getResources().getString(R.string.Security_warning));
                                unsafeCurrent1 = true;
                            }

                            break;
                    }

                    switch (socketStat2) {
                        case 1:
                            btnSkStat2.setImageResource(R.drawable.dot_black);
                            swSk2.setChecked(false);
                            SkAuto2(false);
                            if (statChanged2)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk2.getText() + getString(R.string.has_been_turn_off), blue);
                            break;
                        case 2:
                            btnSkStat2.setImageResource(R.drawable.dot_green);
                            swSk2.setChecked(true);
                            SkAuto2(false);
                            if (statChanged2)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk2.getText() + getString(R.string.has_been_turn_on), blue);
                            break;
                        case 3:
                            btnSkStat2.setImageResource(R.drawable.dot_blue);
                            swSk2.setChecked(false);
                            SkAuto2(true);
                            if (statChanged2)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk2.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 4:
                            btnSkStat2.setImageResource(R.drawable.dot_blue);
                            swSk2.setChecked(true);
                            SkAuto2(true);
                            if (statChanged2)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk2.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 5:
                            btnSkStat2.setImageResource(R.drawable.dot_red);
                            swSk2.setChecked(false);
                            swSk2.setEnabled(false);
                            SkAuto2(false);
                            if (statChanged2) {
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk2.getText() + getString(R.string.abnormal_current), red);
                                makeOreoNotification("Warning", getResources().getString(R.string.Security_warning));
                                unsafeCurrent2 = true;
                            }
                            break;
                    }

                    switch (socketStat3) {
                        case 1:
                            btnSkStat3.setImageResource(R.drawable.dot_black);
                            swSk3.setChecked(false);
                            SkAuto3(false);
                            if (statChanged3)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk3.getText() + getString(R.string.has_been_turn_off), blue);
                            break;
                        case 2:
                            btnSkStat3.setImageResource(R.drawable.dot_green);
                            swSk3.setChecked(true);
                            SkAuto3(false);
                            if (statChanged3)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk3.getText() + getString(R.string.has_been_turn_on), blue);
                            break;
                        case 3:
                            btnSkStat3.setImageResource(R.drawable.dot_blue);
                            swSk3.setChecked(false);
                            SkAuto3(true);
                            if (statChanged3)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk3.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 4:
                            btnSkStat3.setImageResource(R.drawable.dot_blue);
                            swSk3.setChecked(true);
                            SkAuto3(true);
                            if (statChanged3)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk3.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 5:
                            btnSkStat3.setImageResource(R.drawable.dot_red);
                            swSk3.setChecked(false);
                            swSk3.setEnabled(false);
                            SkAuto3(false);
                            if (statChanged3) {
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk3.getText() + getString(R.string.abnormal_current), red);
                                makeOreoNotification("Warning", getResources().getString(R.string.Security_warning));
                                unsafeCurrent3 = true;
                            }
                            break;
                    }

                    switch (socketStat4) {
                        case 1:
                            btnSkStat4.setImageResource(R.drawable.dot_black);
                            swSk4.setChecked(false);
                            SkAuto4(false);
                            if (statChanged4)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk4.getText() + getString(R.string.has_been_turn_off), blue);
                            break;
                        case 2:
                            btnSkStat4.setImageResource(R.drawable.dot_green);
                            swSk4.setChecked(true);
                            SkAuto4(false);
                            if (statChanged4)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk4.getText() + getString(R.string.has_been_turn_on), blue);
                            break;
                        case 3:
                            btnSkStat4.setImageResource(R.drawable.dot_blue);
                            swSk4.setChecked(false);
                            SkAuto4(true);
                            if (statChanged4)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk4.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 4:
                            btnSkStat4.setImageResource(R.drawable.dot_blue);
                            swSk4.setChecked(true);
                            SkAuto4(true);
                            if (statChanged4)
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk4.getText() + getString(R.string.has_been_set_to_auto_mode), blue);
                            break;
                        case 5:
                            btnSkStat4.setImageResource(R.drawable.dot_red);
                            swSk4.setChecked(false);
                            swSk4.setEnabled(false);
                            SkAuto4(false);
                            if (statChanged4) {
                                CustomizedSnackBar(getString(R.string.socket) + "："
                                        + swSk4.getText() + getString(R.string.abnormal_current), red);
                                makeOreoNotification("Warning", getResources().getString(R.string.Security_warning));
                                unsafeCurrent4 = true;
                            }
                            break;
                    }
                } catch (Exception e) {
                    Log.d("e", e + "");
                }
            }
            btDataString.delete(0, btDataString.length());
        }
    }

    private void SkAuto1(boolean b) {
        if (b) {
            btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto1.setTextColor(getResources().getColor(R.color.white));
            AutoOn1 = true; //for button style
            AutoHW1 = true; //for snackbar
        } else {
            btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto1.setTextColor(getResources().getColor(R.color.colorPrimary));
            AutoOn1 = false;
            AutoHW1 = false;
        }
    }

    private void SkAuto2(boolean b) {
        if (b) {
            btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto2.setTextColor(getResources().getColor(R.color.white));
            AutoOn2 = true;
            AutoHW2 = true;
        } else {
            btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto2.setTextColor(getResources().getColor(R.color.colorPrimary));
            AutoOn2 = false;
            AutoHW2 = false;
        }
    }

    private void SkAuto3(boolean b) {
        if (b) {
            btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto3.setTextColor(getResources().getColor(R.color.white));
            AutoOn3 = true;
            AutoHW3 = true;
        } else {
            btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto3.setTextColor(getResources().getColor(R.color.colorPrimary));
            AutoOn3 = false;
            AutoHW3 = false;
        }
    }

    private void SkAuto4(boolean b) {
        if (b) {
            btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto4.setTextColor(getResources().getColor(R.color.white));
            AutoOn4 = true;
            AutoHW4 = true;
        } else {
            btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto4.setTextColor(getResources().getColor(R.color.colorPrimary));
            AutoOn4 = false;
            AutoHW4 = false;
        }
    }

    private void onCreateFirebaseCheck() {
        /*DatabaseReference reason = FirebaseDatabase.getInstance().getReference("test_user1").child("command");
        reason.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StrR = dataSnapshot.getValue().toString();
                if (StrR.equals("a")) {
                    StrR = "管理員未說明。";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/

        dbRef = FirebaseDatabase.getInstance().getReference("2019-02-27 19:27:52");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().toString().equals("timestamp sample")) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getResources().getString(R.string.system_info))
                            .setMessage(getResources().getString(R.string.app_access_deny))
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference("app_info");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Log.d(TAG, "Latest app version: " + dataSnapshot.child("app_version").getValue());
                    int minVer = Integer.parseInt(dataSnapshot.child("minimum_version").getValue().toString());
                    int latestVer = Integer.parseInt(dataSnapshot.child("app_version").getValue().toString());
                    if (minVer > BuildConfig.VERSION_CODE) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getResources().getString(R.string.important_version))
                                .setMessage(
                                        getResources().getString(R.string.current_version) + " " + BuildConfig.VERSION_CODE + "\n" +
                                                getResources().getString(R.string.latest_version) + " " + dataSnapshot.child("app_version").getValue() + "\n" +
                                                getResources().getString(R.string.minimum_version) + " " + dataSnapshot.child("minimum_version").getValue() + "\n" +
                                                getResources().getString(R.string.must_update))
                                .setPositiveButton(R.string.link, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/open?id=1iMd8BCdluwYdOL16fL9vXptsp5kOTGgX"));
                                        startActivity(browserIntent);
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        finish();
                                    }
                                })
                                .show();
                    } else if (latestVer > BuildConfig.VERSION_CODE) {
                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getResources().getString(R.string.find_a_new_version))
                                .setMessage(
                                        getResources().getString(R.string.current_version) + " " + BuildConfig.VERSION_CODE + "\n" +
                                                getResources().getString(R.string.latest_version) + " " + dataSnapshot.child("app_version").getValue() + "\n" +
                                                getResources().getString(R.string.latest_changes) + "\n" + dataSnapshot.child("latest_changes").getValue() + "\n\n" +
                                                getResources().getString(R.string.please_download_new_version))
                                .setPositiveButton(R.string.link, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/open?id=1iMd8BCdluwYdOL16fL9vXptsp5kOTGgX"));
                                        startActivity(browserIntent);
                                    }
                                })
                                .setNeutralButton(R.string.full_update, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jerry960331/SAIHS_ProjectS_2/commits/master"));
                                        startActivity(browserIntent);
                                    }
                                })
                                //.setNeutralButtonIcon(getResources().getDrawable(R.drawable.ic_open_in_new_black_24dp))
                                .show();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button button = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                                Drawable drawable = getResources().getDrawable(R.drawable.ic_open_in_new_black_24dp);

                                // set the bounds to place the drawable a bit right
                                drawable.setBounds((int) (drawable.getIntrinsicWidth() * 0.5), 0, (int) (drawable.getIntrinsicWidth() * 1.5),
                                        drawable.getIntrinsicHeight());
                                button.setCompoundDrawables(null, null, drawable, null);
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.connected_to_db), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.unable_to_check_app_version), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.unable_to_check_app_version), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected && statOnCloud) {
                    imgNetworkStat.setImageResource(R.drawable.dot_green);
                } else {
                    imgNetworkStat.setImageResource(R.drawable.dot_gray);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        //user account
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    userUID = user.getUid();
                    userEmail = user.getEmail();

                    firebase = FirebaseDatabase.getInstance();
                    DatabaseReference dbRefUID = firebase.getReference("UID");
                    dbRefUID.setValue(userUID);


                    firebaseCommand("z");
                    Crashlytics.setUserIdentifier(userUID);
                    statOnCloud = true;
                    Log.d(user.getEmail() + ": ", userUID);
                    dbRef = FirebaseDatabase.getInstance().getReference("users").child(userUID);
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            firebaseUpdateUserData(userName, user.getEmail());
                            firebaseUpdateBSPref();
                            firebaseUpdateAppTitle();
                            imgNetworkStat.setImageResource(R.drawable.dot_green);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    CustomizedSnackBar(getString(R.string.log_in_successfully_by) +" "+ userEmail, green);

                } else {
                    //Toast.makeText(MainActivity.this, getResources().getString(R.string.log_out), Toast.LENGTH_SHORT).show();
                    userEmail = "";
                    userName = "";
                    invalidateOptionsMenu();
                    statOnCloud = false;
                    CustomizedSnackBar(getString(R.string.not_login), green);
                }
            }
        };


        //從資料庫獲取插座狀態
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("BlueStormIII").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!isBTConnected && statOnCloud) {
                    Log.d(TAG, "Data from Firebase" + dataSnapshot.getValue());
                    btDataString.append(dataSnapshot.getValue());
                    mainCtrlLoop(btDataString);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!isBTConnected && statOnCloud) {
                    Log.d(TAG, "Data from Firebase" + dataSnapshot.getValue());
                    btDataString.append(dataSnapshot.getValue());
                    mainCtrlLoop(btDataString);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void firebaseCommand(Object command) {
        if (statOnCloud) {
            firebase = FirebaseDatabase.getInstance();
            dbRef = firebase.getReference("users").child(userUID).child("Blue Storm III").child("command");
            dbRef.setValue(command);

            dbRef = firebase.getReference("command");
            dbRef.setValue(command);
        }
    }

    private void setOnClickListeners() {
        swSk1.setOnClickListener(SwListener);
        swSk2.setOnClickListener(SwListener);
        swSk3.setOnClickListener(SwListener);
        swSk4.setOnClickListener(SwListener);

        swSk1.setOnLongClickListener(skTxChangeListener);
        swSk2.setOnLongClickListener(skTxChangeListener);
        swSk3.setOnLongClickListener(skTxChangeListener);
        swSk4.setOnLongClickListener(skTxChangeListener);

        btnSkStat1.setOnClickListener(SkStatListener1);
        btnSkStat2.setOnClickListener(SkStatListener2);
        btnSkStat3.setOnClickListener(SkStatListener3);
        btnSkStat4.setOnClickListener(SkStatListener4);

        btnSkAlarm.setOnClickListener(SkAlarmListener1);

        btnSkChart1.setOnClickListener(SkChartListener1);
        btnSkChart2.setOnClickListener(SkChartListener2);
        btnSkChart3.setOnClickListener(SkChartListener3);
        btnSkChart4.setOnClickListener(SkChartListener4);

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

        btnConnect = findViewById(R.id.btnConnect);

        imgBTStat = findViewById(R.id.imgBTStat);
        imgNetworkStat = findViewById(R.id.imgNetworkStat);

        btnSkStat1 = findViewById(R.id.btnSkStat1);
        btnSkStat2 = findViewById(R.id.btnSkStat2);
        btnSkStat3 = findViewById(R.id.btnSkStat3);
        btnSkStat4 = findViewById(R.id.btnSkStat4);

        btnSkAlarm = findViewById(R.id.btnSkAlarm);
        btnSkInfo = findViewById(R.id.btnSkInfo);

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

    private Switch.OnLongClickListener skTxChangeListener = new Switch.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            final TextView tx = (TextView) v;
            CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            CustomDialog.functionSelect = "Input";
            CustomDialog.show();
            CustomDialog.edText.setText(tx.getText().toString());
            CustomDialog.setInputDialogResult(new CustomDialogActivity.OnInputDialogResult() {
                @Override
                public void text(String text) {
                    tx.setText(text);
                    firebaseUpdateBSPref();
                }
            });
            return true;
        }
    };

    //插座開關
    private Switch.OnClickListener SwListener = new Switch.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Switch s = (Switch) v;
            String switchText = s.getText().toString();
            final int switchId = s.getId();
            final String switchOnOff;
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
                            switch (switchId) {
                                case R.id.swSk1:
                                    SkAuto1(false);
                                    btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto1.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    unsafeCurrent1 = false;
                                    if (switchOnOff.equals(getString(R.string.open))) {
                                        BT_comm = "a";
                                        firebaseCommand("a");
                                    } else {
                                        BT_comm = "b";
                                        firebaseCommand("b");
                                    }
                                    break;
                                case R.id.swSk2:
                                    SkAuto2(false);
                                    btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    unsafeCurrent2 = false;
                                    if (switchOnOff.equals(getString(R.string.open))) {
                                        BT_comm = "c";
                                        firebaseCommand("c");
                                    } else {
                                        BT_comm = "d";
                                        firebaseCommand("d");
                                    }
                                    break;
                                case R.id.swSk3:
                                    SkAuto3(false);
                                    btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto3.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    unsafeCurrent3 = false;
                                    if (switchOnOff.equals(getString(R.string.open))) {
                                        BT_comm = "e";
                                        firebaseCommand("e");
                                    } else {
                                        BT_comm = "f";
                                        firebaseCommand("f");
                                    }
                                    break;
                                case R.id.swSk4:
                                    SkAuto4(false);
                                    btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                    btnSkAuto4.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    unsafeCurrent4 = false;
                                    if (switchOnOff.equals(getString(R.string.open))) {
                                        BT_comm = "g";
                                        firebaseCommand("g");
                                    } else {
                                        BT_comm = "h";
                                        firebaseCommand("h");
                                    }
                                    break;
                            }
                            if (btConnectedThread != null) {
                                String sendData = BT_comm;
                                btConnectedThread.write(sendData);
                                Log.d("Bluetooth Send Data: ", sendData);
                            }
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
            if (swSk1.isChecked() || unsafeCurrent1) { //開啟或是過載
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = true;
                //CustomDialog.currentNow = Integer.parseInt(current1);
                CustomDialog.currentAve = currentAv1;
                CustomDialog.safeCurrentValue = safeCurrentValue;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "1");
                            unsafeCurrent1 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("b");
                            }
                            firebaseCommand("b");
                            swSk1.setEnabled(true);
                            if (AutoOn1) {
                                SkAuto1(false);
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {
                        getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                });
                CustomDialog.show();

                getCurrentHandler = new Handler(getMainLooper());
                getCurrentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.currentNow = Integer.parseInt(current1);
                        getCurrentHandler.postDelayed(this, 1000);
                    }
                }, 10);
            } else {//關閉
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentStat = getResources().getString(R.string.socket_off);
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "1");
                            unsafeCurrent1 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("b");
                            }
                            firebaseCommand("b");
                            swSk1.setEnabled(true);
                            if (AutoOn1) {
                                SkAuto1(false);
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {

                    }
                });
                CustomDialog.show();
            }


        }
    };
    private Button.OnClickListener SkStatListener2 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if (swSk2.isChecked() || unsafeCurrent2) {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 2;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current2);
                CustomDialog.currentAve = currentAv2;
                CustomDialog.safeCurrentValue = safeCurrentValue;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "2");
                            unsafeCurrent2 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("d");
                            }
                            firebaseCommand("d");
                            swSk2.setEnabled(true);
                            if (AutoOn2) {
                                btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                btnSkAuto2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                AutoOn2 = false;
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {
                        getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                });
                CustomDialog.show();
                getCurrentHandler = new Handler(getMainLooper());
                getCurrentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.currentNow = Integer.parseInt(current2);
                        getCurrentHandler.postDelayed(this, 1000);
                    }
                }, 10);
            } else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 2;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "2");
                            unsafeCurrent2 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("d");
                            }
                            firebaseCommand("d");
                            swSk2.setEnabled(true);
                            if (AutoOn2) {
                                btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                btnSkAuto2.setTextColor(getResources().getColor(R.color.colorPrimary));
                                AutoOn2 = false;
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {

                    }
                });
                CustomDialog.show();
            }
        }
    };
    private Button.OnClickListener SkStatListener3 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if (swSk3.isChecked() || unsafeCurrent3) {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 3;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current3);
                CustomDialog.currentAve = currentAv3;
                CustomDialog.safeCurrentValue = safeCurrentValue;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "3");
                            unsafeCurrent3 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("f");
                            }
                            firebaseCommand("f");
                            swSk3.setEnabled(true);
                            if (AutoOn3) {
                                btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                btnSkAuto3.setTextColor(getResources().getColor(R.color.colorPrimary));
                                AutoOn3 = false;
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {
                        //getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                });
                CustomDialog.show();
                getCurrentHandler = new Handler(getMainLooper());
                getCurrentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.currentNow = Integer.parseInt(current3);
                        getCurrentHandler.postDelayed(this, 1000);
                    }
                }, 10);
            } else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 3;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "3");
                            unsafeCurrent3 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("f");
                            }
                            firebaseCommand("f");
                            swSk3.setEnabled(true);
                            if (AutoOn3) {
                                btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                btnSkAuto3.setTextColor(getResources().getColor(R.color.colorPrimary));
                                AutoOn3 = false;
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {

                    }
                });
                CustomDialog.show();
            }
        }
    };
    private Button.OnClickListener SkStatListener4 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if (swSk4.isChecked() || unsafeCurrent4) {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 4;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current4);
                CustomDialog.currentAve = currentAv4;
                CustomDialog.safeCurrentValue = safeCurrentValue;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "4");
                            unsafeCurrent4 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("h");
                            }
                            firebaseCommand("h");
                            swSk4.setEnabled(true);
                            if (AutoOn4) {
                                btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                btnSkAuto4.setTextColor(getResources().getColor(R.color.colorPrimary));
                                AutoOn4 = false;
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {
                        getCurrentHandler.removeCallbacksAndMessages(null);
                    }
                });
                CustomDialog.show();
                getCurrentHandler = new Handler(getMainLooper());
                getCurrentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomDialog.currentNow = Integer.parseInt(current4);
                        getCurrentHandler.postDelayed(this, 1000);
                    }
                }, 10);
            } else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 4;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.setStatDialogResult(new CustomDialogActivity.OnStatDialogResult() {
                    @Override
                    public void warningClear(boolean warningClear) {
                        if (warningClear) {
                            Toast.makeText(MainActivity.this, "插座已解鎖", Toast.LENGTH_SHORT).show();
                            Log.d("warningCleared ", "4");
                            unsafeCurrent4 = false;
                            if (btConnectedThread != null) {
                                btConnectedThread.write("h");
                            }
                            firebaseCommand("h");
                            swSk4.setEnabled(true);
                            if (AutoOn4) {
                                btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto));
                                btnSkAuto4.setTextColor(getResources().getColor(R.color.colorPrimary));
                                AutoOn4 = false;
                            }
                        }
                    }

                    @Override
                    public void finish(String result) {

                    }
                });
                CustomDialog.show();
            }
        }
    };

    //鬧鐘OnClick
    private Button.OnClickListener SkAlarmListener1 = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (statOnCloud) {
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
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.alarm_not_set), Toast.LENGTH_SHORT).show();
                        cancelAlarm(cal);
                    }
                });
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.please_log_in))
                        .setMessage(getString(R.string.please_log_in_desc))
                        .setPositiveButton(getString(R.string.login), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                login(menu.findItem(R.id.action_login));
                            }
                        })
                        .show();
            }
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

    //幫你打開藍牙
    public void setBluetoothEnable(Boolean enable) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
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
        } catch (Exception e) {
            Toast.makeText(this, R.string.BTCrash,
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (btAdapter != null && btAdapter.isEnabled()) {
                Connect();
            }
        }
    }

    //連線按鈕 OnClick
    private Button.OnClickListener btnConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Connect();
        }
    };

    public void Connect() {

        setBluetoothEnable(true);
        //todo 藍牙裝置選擇界面或自動搜尋
        final String address = "98:D3:33:81:25:60"; //HC05的address
        final String name = "SBLUE";

        try {
            if (!btAdapter.isEnabled()) {
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.BTCrash,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isBTConnected) {
            return;
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.connecting_with_dots));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

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
                    btConnectedThread = new ConnectedThread(btSocket);
                    btConnectedThread.start();
                    //開啟新執行緒顯示連接裝置名稱
                    btHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();

                    //藍牙連接成功

                    btConnectedThread.write("z"); //成功後傳值

                    isBTConnected = true;

                    IntentFilter filter = new IntentFilter();

                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
                    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                    MainActivity.this.registerReceiver(mReceiver, filter);
                    //FunctionSetEnable(true);
                }
            }
        }.start();
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
                Log.d("bt stat onReceive", "Device found");
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                Log.d("bt stat onReceive", "connected");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
                Log.d("bt stat onReceive", "Searching done");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                Log.d("bt stat onReceive", "Device is about to disconnect");

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Log.d("bt stat onReceive", "Device has disconnected");
                Toast.makeText(MainActivity.this, getString(R.string.device_has_disconnected), Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.connection_status))
                        .setMessage(getString(R.string.device_has_disconnected))
                        .show();

                allTurnToUnconnected();
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        CustomizedSnackBar("Bluetooth off", green);
                        allTurnToUnconnected();
                        Disconnect();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this, "Turning Bluetooth off...", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        CustomizedSnackBar("Bluetooth on", green);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this, "Turning Bluetooth on...", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    private void allTurnToUnconnected() {
        btnConnect.setVisibility(View.VISIBLE);
        isBTConnected = false;
        imgBTStat.setImageResource(R.drawable.dot_gray);
        btnSkStat1.setImageResource(R.drawable.dot_gray);
        btnSkStat2.setImageResource(R.drawable.dot_gray);
        btnSkStat3.setImageResource(R.drawable.dot_gray);
        btnSkStat4.setImageResource(R.drawable.dot_gray);
        swSk1.setChecked(false);
        swSk2.setChecked(false);
        swSk3.setChecked(false);
        swSk4.setChecked(false);
        SkAuto1(false);
        SkAuto2(false);
        SkAuto3(false);
        SkAuto4(false);
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
                        //Log.d("Read Buffer", bytes + "");
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
                } catch (RuntimeException e) {
                    Log.e(TAG, "20%", e);
                    e.printStackTrace();
                    //todo 20%
                    Crashlytics.setString(TAG, "BT 20% RTe");
                    Crashlytics.logException(e);
                    Connect();
                } catch (Exception e) {
                    Log.e(TAG, "20%-2", e);
                    e.printStackTrace();
                    //todo 其他20%
                    Crashlytics.setString(TAG, "BT 20% e");
                    Crashlytics.logException(e);
                    Connect();
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        void write(String input) {
            if (btConnectedThread != null) {

                byte[] bytes = input.getBytes();
                try {
                    mmOutStream.write(bytes);
                } catch (IOException ignored) {
                }
                try {
                    mmOutStream.write(bytes);
                } catch (IOException ignored) {
                }
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
    public void auto1(View view) {
        if (unsafeCurrent1) {
            CustomizedSnackBar(getString(R.string.please_clear_abnormal_current_first), red);
        } else {
            if (!AutoOn1) {
                SkAuto1(true);
                //swSk1.setEnabled(false);
                AutoTimerRepeatNOPE = false;
                if (btConnectedThread != null) {
                    btConnectedThread.write("i");
                }
                firebaseCommand("i");
            } else {
                SkAuto1(false);
                //swSk1.setEnabled(true);
                if (btConnectedThread != null) {
                    btConnectedThread.write("m");
                }
                firebaseCommand("m");
            }
        }
    }

    public void auto2(View view) {
        if (unsafeCurrent2) {
            CustomizedSnackBar(getString(R.string.please_clear_abnormal_current_first), red);
        } else if (!AutoOn2) {
            SkAuto2(true);
            //swSk2.setEnabled(false);
            AutoTimerRepeatNOPE = false;
            if (btConnectedThread != null) {
                btConnectedThread.write("j");
            }
            firebaseCommand("j");
        } else {
            SkAuto2(false);
            //swSk2.setEnabled(true);
            if (btConnectedThread != null) {
                btConnectedThread.write("n");
            }
            firebaseCommand("n");
        }
    }

    public void auto3(View view) {
        if (unsafeCurrent3) {
            CustomizedSnackBar(getString(R.string.please_clear_abnormal_current_first), red);
        } else if (!AutoOn3) {
            SkAuto3(true);
            //swSk3.setEnabled(false);
            AutoTimerRepeatNOPE = false;
            if (btConnectedThread != null) {
                btConnectedThread.write("k");
            }
            firebaseCommand("k");
        } else {
            SkAuto3(false);
            //swSk3.setEnabled(true);
            if (btConnectedThread != null) {
                btConnectedThread.write("o");
            }
            firebaseCommand("o");
        }
    }

    public void auto4(View view) {
        if (unsafeCurrent4) {
            CustomizedSnackBar(getString(R.string.please_clear_abnormal_current_first), red);
        } else if (!AutoOn4) {
            SkAuto4(true);
            //swSk4.setEnabled(false);
            AutoTimerRepeatNOPE = false;
            if (btConnectedThread != null) {
                btConnectedThread.write("l");
            }
            firebaseCommand("l");
        } else {
            SkAuto4(false);
            //swSk4.setEnabled(true);
            if (btConnectedThread != null) {
                btConnectedThread.write("p");
            }
            firebaseCommand("p");
        }
    }

    private void startAlarm(Calendar calendar) {
        Calendar nowCal = Calendar.getInstance(TimeZone.getDefault());
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        //just for test
        /*calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);*/

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putBooleanArray("socketFromMain", alarmSocketSelect);
        bundle.putString("purposeFromMain", alarmPurpose);
        bundle.putString("userUID", userUID);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, getResources().getString(R.string.alarm_have_been_set_to) + "\n" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(calendar.getTime()), Toast.LENGTH_SHORT).show();

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
            MaterialShowcaseView.resetSingleUse(MainActivity.this, "function");
            MaterialShowcaseView.resetSingleUse(MainActivity.this, "menu");

            //MaterialShowcaseView.resetSingleUse(MainActivity.this, "alarm");

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
            txLog.setText("     +" + getString(R.string.log_cleared));
        }
    };

    public void CustomizedSnackBar(String SnackBarText, int color) {
        snackbar = Snackbar.make(findViewById(android.R.id.content), SnackBarText, Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.dismiss), null);
        snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(color);
        snackBarTxV = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        menu.findItem(R.id.action_statOnCloud).setChecked(false);
        menu.findItem(R.id.action_dev).setChecked(true);
        devMode = true;
        return true;
    }

    public boolean onPrepareOptionsMenu(final Menu menu) {

        if (!devMode) {
            menu.findItem(R.id.action_bt).setVisible(false);
            menu.findItem(R.id.action_notification).setVisible(false);
            menu.findItem(R.id.action_destroy).setVisible(false);
            menu.findItem(R.id.action_log).setVisible(false);
            menu.findItem(R.id.action_devData).setVisible(false);
        } else {
            menu.findItem(R.id.action_bt).setVisible(true);
            menu.findItem(R.id.action_notification).setVisible(true);
            menu.findItem(R.id.action_destroy).setVisible(true);
            menu.findItem(R.id.action_log).setVisible(true);
            menu.findItem(R.id.action_devData).setVisible(true);
        }

        if (userName == null){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    if (userName.equals("")) {
                        menu.findItem(R.id.action_login).setTitle(R.string.login);
                    } else {
                        menu.findItem(R.id.action_login).setTitle(userName);
                    }
                    Log.d("onPrepareMenu-lag", userName);
                }
            }, 500);
        }else {
            if (userName.equals("")) {
                menu.findItem(R.id.action_login).setTitle(R.string.login);
            } else {
                menu.findItem(R.id.action_login).setTitle(userName);
            }
            Log.d("onPrepareOptionsMenu", userName);
        }
        menu.findItem(R.id.action_statOnCloud).setChecked(statOnCloud);

        /*MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "menu");
        View action_login = findViewById(R.id.action_login);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(action_login)
                        .setDismissText("了解")
                        .setContentText("登入後即可使用網路控制及雲端儲存設定")
                        .withRectangleShape()
                        .build()
        );
        View action_extra = findViewById(R.id.action_extra);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(action_extra)
                        .setDismissText("了解")
                        .setContentText("更多設定可於此調整")
                        .withRectangleShape()
                        .build()
        );
        sequence.start();*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        FirebaseUser user;
        switch (id) {
            case R.id.action_login:
                login(item);
                break;
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
                Calendar calendar = Calendar.getInstance();
                //just for test
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 10);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);

                Bundle bundle = new Bundle();
                bundle.putString("smoke", "smoke");
                intent.putExtras(bundle);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                //makeOreoNotification("Warning", getResources().getString(R.string.Security_warning));
                break;
            case R.id.action_dev:
                item.setChecked(!item.isChecked());
                devMode = item.isChecked();
                break;
            case R.id.action_statOnCloud:
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    item.setChecked(!item.isChecked());
                    statOnCloud = item.isChecked();
                    // TODO: 2019/1/30 把擷取插座狀態的部分關掉
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.please_log_in))
                            .setMessage(getString(R.string.please_log_in_desc))
                            .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    login(menu.findItem(R.id.action_login));
                                }
                            })
                            .show();
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
            case R.id.action_extra:
                extraSettings();
                break;
            case R.id.action_devData:
                item.setChecked(!item.isChecked());
                devModeValue = item.isChecked();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void login(final MenuItem item) {
        final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
        Log.d(TAG + " Item: ", "login Prepare");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "onOptionsItemSelected: login");
            CustomDialog.functionSelect = "Login";
            CustomDialog.show();
            CustomDialog.setLoginDialogResult(new CustomDialogActivity.OnLoginDialogResult() {
                @Override
                public void userData(final String email, final String password) {

                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage(getResources().getString(R.string.logging_in));
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            progressDialog.dismiss();
                                            try {
                                                if (!task.isSuccessful()) {
                                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                                    Log.d(TAG, e + "");
                                                    if (!e.toString().equals(getString(R.string.firebase_err_invalid_passwd))) {
                                                        createNewAccount(email, password, item);
                                                    } else {
                                                        CustomizedSnackBar(getString(R.string.login_fail_wrong_password), red);
                                                    }
                                                } else if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this,
                                                            getResources().getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                                                    firebaseDownloadUserDataAndBSPref(email);

                                                }
                                            }catch (Exception e){
                                                Toast.makeText(MainActivity.this,
                                                        getResources().getString(R.string.log_in_fail), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    };
                    t.start();
                }

                @Override
                public void logOut(boolean logOut) {

                }

                @Override
                public void userPref(String name, String device) {

                }

            });
            CustomDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(MainActivity.this, R.string.cancelled, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            CustomDialog.functionSelect = "Logged in";
            CustomDialog.show();
            CustomDialog.txUserDevice.setText(userDevice);
            CustomDialog.setLoginDialogResult(new CustomDialogActivity.OnLoginDialogResult() {
                @Override
                public void userData(String email, String password) {

                }

                @Override
                public void logOut(boolean logOut) {
                    if (logOut) {
                        auth.signOut();
                        userEmail = "";
                        userName = "";
                        userDevice = "";
                        invalidateOptionsMenu();
                        CustomizedSnackBar(getString(R.string.log_out), green);
                        Log.d(TAG, getString(R.string.log_out));
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getString(R.string.delete_local_user_data))
                                .setMessage(getString(R.string.delete_local_user_data_desc))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        swSk1.setText(getString(R.string.socket_1));
                                        swSk2.setText(getString(R.string.socket_2));
                                        swSk3.setText(getString(R.string.socket_3));
                                        swSk4.setText(getString(R.string.socket_4));
                                        safeCurrentValue = 5000;
                                        userDevice = "Blue Storm III";
                                        appTitle = getString(R.string.app_name);
                                        CustomizedSnackBar(getString(R.string.data_clear), green);
                                        onStop();
                                        finish();
                                        Intent i = getIntent();
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onStop();
                                        finish();
                                        Intent i = getIntent();
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void userPref(String name, String device) {
                    userName = name;
                    firebaseUpdateUserData(name, null);
                    firebase = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = firebase.getReference("users").child(userUID).child("Blue Storm III").child("user_pref");
                    Map<String, Object> data = new HashMap<>();
                    data.put("device_name", device);
                    userDevice = device;
                    myRef.updateChildren(data);
                    invalidateOptionsMenu();
                }

            });
            if (userEmail != null && userName != null) {
                CustomDialog.txUserName.setText(userName);
                CustomDialog.txUserEmail.setText(userEmail);
            }

            Log.d(TAG, "onOptionsItemSelected: Logged in");
        }
    }

    private void firebaseDownloadUserDataAndBSPref(final String email) {
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    userName = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                    userEmail = Objects.requireNonNull(dataSnapshot.child("Email").getValue()).toString();
                    appTitle = Objects.requireNonNull(dataSnapshot.child("App_Title").getValue()).toString();
                    userDevice = Objects.requireNonNull(dataSnapshot.child("Blue Storm III").child("user_pref").child("device_name").getValue()).toString();
                    swSk1.setText(Objects.requireNonNull(dataSnapshot.child("Blue Storm III").child("user_pref").child("Outlet1").getValue()).toString());
                    swSk2.setText(Objects.requireNonNull(dataSnapshot.child("Blue Storm III").child("user_pref").child("Outlet2").getValue()).toString());
                    swSk3.setText(Objects.requireNonNull(dataSnapshot.child("Blue Storm III").child("user_pref").child("Outlet3").getValue()).toString());
                    swSk4.setText(Objects.requireNonNull(dataSnapshot.child("Blue Storm III").child("user_pref").child("Outlet4").getValue()).toString());
                    safeCurrentValue = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Blue Storm III").child("user_pref").child("safeCurrentValue").getValue()).toString());
                    invalidateOptionsMenu();
                    CustomizedSnackBar(getString(R.string.log_in_successfully_by) + email, green);

                } catch (Exception e) {
                    Log.d("userDataCompare: ", "firebase data missing");
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.pref_sync))
                            .setMessage(getString(R.string.firebase_data_missing_desc))
                            .setPositiveButton(getString(R.string.confirm), null)
                            .setCancelable(false)
                            .show();
                    userEmail = email;
                    userName = email.substring(0, email.indexOf("@"));
                    firebaseUpdateUserData(userName, email);
                    firebaseUpdateBSPref();
                    firebaseUpdateAppTitle();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void firebaseUpdateUserData(String username, String email) {
        firebase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebase.getReference("users").child(userUID);
        Map<String, Object> data = new HashMap<>();

        if (email != null && !email.equals("")) {
            data.put("Email", email);
        }
        Log.d(TAG, "firebaseUpdateUserData: " + username);
        if (username != null && !username.equals("")) {
            data.put("username", username);
        } else if (username != null && username.equals("")) {
            /*userName = email.substring(0, email.indexOf("@"));
            data.put("username", userName);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.pref_sync))
                    .setMessage(getString(R.string.pref_user_name_missing_desc)+ ": " + userName)
                    .setPositiveButton(getString(R.string.confirm), null)
                    .show();*/
        }
        myRef.updateChildren(data);
        invalidateOptionsMenu();
    }

    private void firebaseUpdateBSPref() {
        if (statOnCloud) {
            firebase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebase.getReference("users").child(userUID).child("Blue Storm III").child("user_pref");
            Map<String, Object> data = new HashMap<>();
            data.put("Outlet1", swSk1.getText().toString());
            data.put("Outlet2", swSk2.getText().toString());
            data.put("Outlet3", swSk3.getText().toString());
            data.put("Outlet4", swSk4.getText().toString());
            data.put("safeCurrentValue", safeCurrentValue);
            data.put("device_name", userDevice);
            myRef.updateChildren(data);
        }
    }

    private void firebaseUpdateAppTitle() {
        if (statOnCloud) {
            firebase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebase.getReference("users").child(userUID);
            Map<String, Object> data = new HashMap<>();

            data.put("App_Title", appTitle);
            myRef.updateChildren(data);
        }
    }

    private void createNewAccount(final String email, final String password, final MenuItem item) {
        //create new account
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.confirm)
                .setMessage(getString(R.string.account_not_found_description))
                .setPositiveButton(getString(R.string.Sign_up), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                        String message = task.isSuccessful() ? "Sign up successfully" : "Sign up failed";
                                        String strErr = "";
                                        if (e != null) {
                                            strErr = e.toString().substring(66);
                                        }
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setMessage(message + "\n" + strErr)
                                                .setPositiveButton(R.string.confirm, null)
                                                .show();
                                        if (message.equals("Sign up successfully")) {
                                            String menuText = email.substring(0, email.indexOf("@"));
                                            item.setTitle(menuText);
                                            userEmail = email;
                                            userName = email.substring(0, email.indexOf("@"));
                                            firebaseUpdateUserData(userName, email);
                                            firebaseUpdateBSPref();
                                            firebaseUpdateAppTitle();
                                        } else {
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    void extraSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.extra_settings);
        final String[] extraSet = getResources().getStringArray(R.array.extraSet);
        builder.setItems(extraSet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
                Drawable drawable = getResources().getDrawable(R.drawable.icon_warning_64dp);
                switch (which) {
                    case 0: //更改App標題
                        CustomDialog.functionSelect = "Input";
                        CustomDialog.show();
                        CustomDialog.edText.setText(appTitle);
                        CustomDialog.setInputDialogResult(new CustomDialogActivity.OnInputDialogResult() {
                            @Override
                            public void text(String text) {
                                appTitle = text;
                                onStop();
                                finish();
                                Intent i = getIntent();
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });
                        CustomDialog.txWarning.setVisibility(View.VISIBLE);
                        CustomDialog.txWarning.setText(" " + getString(R.string.app_will_restart));

                        drawable.setBounds(0, 0, 64, 64);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
                        CustomDialog.txWarning.setCompoundDrawables(drawable, null, null, null);//只放左边
                        break;
                    case 1: //調整限制電流
                        CustomDialog.functionSelect = "Input";
                        CustomDialog.show();
                        CustomDialog.edText.setText(safeCurrentValue + "");
                        CustomDialog.setInputDialogResult(new CustomDialogActivity.OnInputDialogResult() {
                            @Override
                            public void text(String text) {
                                try {
                                    safeCurrentValue = Integer.parseInt(text);

                                    onStop();
                                    finish();
                                    Intent i = getIntent();
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, getString(R.string.must_enter_a_value), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        CustomDialog.edText.setHint(getString(R.string.please_enter_a_value_ma));
                        CustomDialog.errText = getString(R.string.must_enter_a_value);
                        CustomDialog.txWarning.setVisibility(View.VISIBLE);
                        CustomDialog.txWarning.setText(getResources().getString(R.string.app_will_restart));
                        drawable.setBounds(0, 0, 64, 64);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
                        CustomDialog.txWarning.setCompoundDrawables(drawable, null, null, null);//只放左边
                        break;
                    case 2:
                        setLocale();
                        break;
                    case 3:
                        //Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.send_feedback_desc));
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    case 4:
                        if (showFSA){
                            //hide
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(getString(R.string.to_hide_FSA))
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showFSA = false;
                                            Toast.makeText(MainActivity.this, getString(R.string.set_up_will_be_apply_on_the_next_run), Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();
                        }else {
                            //show
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(getString(R.string.to_show_FSA))
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showFSA = true;
                                            Toast.makeText(MainActivity.this, getString(R.string.set_up_will_be_apply_on_the_next_run), Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .show();
                        }



                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setLocale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.language));
        final String[] extraSet = {getResources().getString(R.string.system_lang), "正體中文", "English", "日本語"};
        builder.setItems(extraSet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Locale myLocale = Locale.getDefault();
                switch (which) {
                    case 0:
                        break;
                    case 1:
                        myLocale = new Locale("zh", "TW");
                        break;
                    case 2:
                        myLocale = new Locale("en", "US");
                        break;
                    case 3:
                        myLocale = new Locale("jp", "JP");
                        break;
                }

                final Locale finalMyLocale = myLocale;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.confirm)
                        .setMessage(getResources().getString(R.string.app_will_restart))
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Resources res = getResources();
                                DisplayMetrics dm = res.getDisplayMetrics();
                                Configuration conf = res.getConfiguration();
                                conf.setLocale(finalMyLocale);
                                res.updateConfiguration(conf, dm);
                                language = finalMyLocale.getLanguage();
                                country = finalMyLocale.getCountry();
                                onStop();
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        View v = findViewById(R.id.appBarLayout);
        CoordinatorLayout.LayoutParams loparams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        CoordinatorLayout layout = findViewById(R.id.coordinatorLayout);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            // Get our View (TextView or anything) object:

            // Get params:
/*
            loparams.height = 200;
            loparams.width = layout.getHeight();
            v.setLayoutParams(loparams);*/
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            /*loparams.height = 500;
            loparams.width = layout.getWidth();
            v.setLayoutParams(loparams);*/
        }
    }

    void makeOreoNotification(String channelId, String channelName) {
        final int NOTIFICATION_ID = 8;

        NotificationManager manager = getNotificationManager(channelId, channelName);

        switch (channelId) {
            case "Warning": {
                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

                    // Create a notification and set the notification channel.
                    Notification notification = new Notification.Builder(MainActivity.this)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationText)
                            .setSmallIcon(R.drawable.icon_notification_blue_stormIII)
                            .setChannelId(channelId)
                            .build();
                }*/

                //產生通知

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.icon_notification_blue_storm_iii)
                                .setContentTitle(notificationTitle)
                                .setContentText(notificationText)
                                .setColor(getResources().getColor(R.color.colorPrimary))
                                .setPriority(2)
                                .setWhen(System.currentTimeMillis())
                                .setChannelId(channelId);  //設定頻道ID

                //送出通知
                manager.notify(1, builder.build());

                break;
            }
            case "test": {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.icon_notification_blue_storm_iii)
                                .setContentTitle("Alarm")
                                .setContentText("test")
                                .setColor(getResources().getColor(R.color.colorPrimary))
                                .setPriority(2)
                                .setWhen(System.currentTimeMillis())
                                .setChannelId(channelId);  //設定頻道ID

                //送出通知
                manager.notify(1, builder.build());
                break;
            }
            default:
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                break;
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
        super.onStart();
        auth.addAuthStateListener(authListener);
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
        SharedPreferences pref;
        pref = getSharedPreferences("alarm1", MODE_PRIVATE);
        pref.edit()
                .putBoolean("isAlarmOn1", isAlarmOn1)
                .putString("alarmSetTime1", alarmSetTime1)
                .putString("alarmSetSchedule1", schedule1)
                .apply();
        pref = getSharedPreferences("user", MODE_PRIVATE);
        pref.edit()
                .putString("user_email", userEmail)
                .putString("user_name", userName)
                .putString("user_device_BS", userDevice)
                .putString("socket1", swSk1.getText().toString())
                .putString("socket2", swSk2.getText().toString())
                .putString("socket3", swSk3.getText().toString())
                .putString("socket4", swSk4.getText().toString())
                .putString("appTitle", appTitle)
                .putInt("safeCurrentValue", safeCurrentValue)
                .putBoolean("showFSA", showFSA)
                .putString("language", language)
                .putString("country", country)
                .apply();
        Log.d("onStop", language + country);

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.save_data_to_phone), Toast.LENGTH_SHORT).show();

        auth.removeAuthStateListener(authListener);
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.d("MainActivity:", "onDestroy");
        btHandler.removeCallbacksAndMessages(null);
        Disconnect(); //BT disconnect
        if (isBTConnected) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}