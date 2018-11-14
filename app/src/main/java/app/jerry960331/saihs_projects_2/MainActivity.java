package app.jerry960331.saihs_projects_2;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


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
            swConnectionMethod;

    private Button
            btnConnect,
            btnSkAuto1, btnSkAuto2, btnSkAuto3 , btnSkAuto4;

    String connectionMethod = "Bluetooth";

    private ProgressDialog progress;

    private TextView txConnectStat, txLog;
    private ImageView imageConnectStat;

    String notificationTitle = "安全警示",
           notificationText = "插座電流狀況異常！請立即前往查看";

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

    boolean isBTConnected = false;
    boolean isWiFiConnected = false;
    boolean confirmSwitch;
    boolean AutoOn1 = false;
    boolean AutoOn2 = false;
    boolean AutoOn3 = false;
    boolean AutoOn4 = false;
    boolean devMode = false;
    String PIR;
    String current1 = "0", current2 = "0", current3 = "0", current4 = "0";
    Double currentAv1 = 0.0, currentAv2 = 0.0, currentAv3 = 0.0, currentAv4 = 0.0;
    Integer i = 0;

    //color
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
        setOnClickListeners();
        txVStat.bringToFront();
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        FunctionSetEnable(false);



        btHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if(msg.what == MESSAGE_READ){
                    try{
                        String readMessage = new String((byte[]) msg.obj, "UTF-8");
                        btDataString.append(readMessage);
                    }catch (UnsupportedEncodingException uee){
                        uee.printStackTrace();
                    }

                    int endOfLineIndex = btDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        //tvSB.setText(String.value)
                        if (btDataString.charAt(0) == '#') {
                            PIR = btDataString.substring(1, 2);
                            current1 = btDataString.substring(3, 8);
                            current2 = btDataString.substring(9, 14);
                            current3 = btDataString.substring(15, 20);
                            current4 = btDataString.substring(21, 26);

                            i++;
                            currentAv1 += Double.parseDouble(current1) / i;
                            currentAv2 += Double.parseDouble(current2) / i;
                            currentAv3 += Double.parseDouble(current3) / i;
                            currentAv4 += Double.parseDouble(current4) / i;
                            if(PIR == "1") {
                                try{
                                    TimeCountDown.cancel();
                                }catch (Exception e){}
                            }
                            else{
                                if (AutoOn1 || AutoOn2 || AutoOn3 || AutoOn4) {
                                    try {
                                        TimeCountDown.start();
                                    } catch (Exception e) {
                                    }
                                }
                            }

                        }
                        btDataString.delete(0, btDataString.length());
                    }
                }
                if (msg.what == CONNECTING_STATUS){
                    try{
                        if (msg.arg1 == 1)
                            Toast.makeText(getApplicationContext(), R.string.connected_successfully, Toast.LENGTH_SHORT).show();

                        else
                            Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                            imageConnectStat.setVisibility(View.VISIBLE);
                            txConnectStat.setText(R.string.failed);
                    }catch (Exception e) {
                    }
                }
                if (btConnectedThread != null){
                    String sendData = BT_comm;
                    btConnectedThread.write(sendData);
                }
            }
        };
    }

    private void setOnClickListeners() {
        swSk1.setOnClickListener(SwListener);
        swSk2.setOnClickListener(SwListener);
        swSk3.setOnClickListener(SwListener);
        swSk4.setOnClickListener(SwListener);

        btnSkStat1.setOnClickListener(SkStatListener1);
        btnSkStat2.setOnClickListener(SkStatListener2);
        btnSkStat3.setOnClickListener(SkStatListener3);
        btnSkStat4.setOnClickListener(SkStatListener4);

        btnSkAlarm1.setOnClickListener(SkAlarmListener1);
        btnSkAlarm2.setOnClickListener(SkAlarmListener2);
        btnSkAlarm3.setOnClickListener(SkAlarmListener3);
        btnSkAlarm4.setOnClickListener(SkAlarmListener4);

        btnSkChart1.setOnClickListener(SkChartListener1);
        btnSkChart2.setOnClickListener(SkChartListener2);
        btnSkChart3.setOnClickListener(SkChartListener3);
        btnSkChart4.setOnClickListener(SkChartListener4);

        swConnectionMethod.setOnClickListener(SwConnectionMethodListener);
    }

    //ctrl+alt+M
    private void findViews() {
        swSk1 = findViewById(R.id.swSk1);
        swSk2 = findViewById(R.id.swSk2);
        swSk3 = findViewById(R.id.swSk3);
        swSk4 = findViewById(R.id.swSk4);
        swConnectionMethod = findViewById(R.id.swConnectionMethod);

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
                                    }
                                    break;
                                case R.id.swSk2:
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
                                    }
                                    break;
                                case R.id.swSk4:
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
                            if (btConnectedThread != null){
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
    private Button.OnClickListener SkStatListener1 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if(!swSk1.isChecked()){ //沒開
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentStat = getResources().getString(R.string.socket_off);
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0 ;
                CustomDialog.show();
            }else {//有開
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = true;
                CustomDialog.currentNow = Integer.parseInt(current1);
                CustomDialog.currentAve = currentAv1;
                CustomDialog.show();
            }
        }
    };
    private Button.OnClickListener SkStatListener2 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if(!swSk2.isChecked()){
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.show();
            }else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current2);
                CustomDialog.currentAve = currentAv2;
                CustomDialog.show();
            }
        }
    };
    private Button.OnClickListener SkStatListener3 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if(!swSk3.isChecked()){
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.show();
            }else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current3);
                CustomDialog.currentAve = currentAv3;
                CustomDialog.show();
            }
        }
    };
    private Button.OnClickListener SkStatListener4 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            if(!swSk4.isChecked()){
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = 0;
                CustomDialog.currentAve = 0.0;
                CustomDialog.show();
            }else {
                CustomDialog.functionSelect = "Stat";
                CustomDialog.socketSelect = 1;
                CustomDialog.isSWOn = false;
                CustomDialog.currentNow = Integer.parseInt(current4);
                CustomDialog.currentAve = currentAv4;
                CustomDialog.show();

            }

        }
    };

    //鬧鐘OnClick
    private Button.OnClickListener SkAlarmListener1 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            CustomDialog.functionSelect = "Alarm";
            CustomDialog.socketSelect = 1;




        }
    };
    private Button.OnClickListener SkAlarmListener2 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){



        }
    };
    private Button.OnClickListener SkAlarmListener3 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){



        }
    };
    private Button.OnClickListener SkAlarmListener4 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){



        }
    };



    //表格OnClick
    private Button.OnClickListener SkChartListener1 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            final SimpleLineChart ChartVal = new SimpleLineChart(MainActivity.this);
            CustomDialog.functionSelect = "Chart";
            String[] xItem = {"1","2","3","4","5","6","7"};
            String[] yItem = {"29mA","28mA","27mA","26mA","25mA"};
            int[] currentValue = new int[xItem.length];
            CustomDialog.xChart = xItem;
            CustomDialog.yChart = yItem;
            for(int i = 0;i<xItem.length;i++){
                currentValue[i] = (int)(Math.random()*5);
            }
            CustomDialog.currentValue = currentValue;
            CustomDialog.show();
        }
    };
    private Button.OnClickListener SkChartListener2 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            final SimpleLineChart ChartVal = new SimpleLineChart(MainActivity.this);
            CustomDialog.functionSelect = "Chart";
            String[] xItem = {"1","2","3","4","5","6","7"};
            String[] yItem = {"29mA","28mA","27mA","26mA","25mA"};
            int[] currentValue = new int[xItem.length];
            CustomDialog.xChart = xItem;
            CustomDialog.yChart = yItem;
            for(int i = 0;i<xItem.length;i++){
                currentValue[i] = (int)(Math.random()*5);
            }
            CustomDialog.currentValue = currentValue;
            CustomDialog.show();
        }
    };
    private Button.OnClickListener SkChartListener3 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            final SimpleLineChart ChartVal = new SimpleLineChart(MainActivity.this);
            CustomDialog.functionSelect = "Chart";
            String[] xItem = {"1","2","3","4","5","6","7"};
            String[] yItem = {"29mA","28mA","27mA","26mA","25mA"};
            int[] currentValue = new int[xItem.length];
            CustomDialog.xChart = xItem;
            CustomDialog.yChart = yItem;
            for(int i = 0;i<xItem.length;i++){
                currentValue[i] = (int)(Math.random()*5);
            }
            CustomDialog.currentValue = currentValue;
            CustomDialog.show();
        }
    };
    private Button.OnClickListener SkChartListener4 = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            final CustomDialogActivity CustomDialog = new CustomDialogActivity(MainActivity.this);
            final SimpleLineChart ChartVal = new SimpleLineChart(MainActivity.this);
            CustomDialog.functionSelect = "Chart";
            String[] xItem = {"1","2","3","4","5","6","7"};
            String[] yItem = {"29mA","28mA","27mA","26mA","25mA"};
            int[] currentValue = new int[xItem.length];
            CustomDialog.xChart = xItem;
            CustomDialog.yChart = yItem;
            for(int i = 0;i<xItem.length;i++){
                currentValue[i] = (int)(Math.random()*5);
            }
            CustomDialog.currentValue = currentValue;
            CustomDialog.show();
        }
    };



    //選擇WiFi、藍牙的連線方式
    private Switch.OnClickListener SwConnectionMethodListener = new Switch.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Switch s = (Switch) v;
            if(!isWiFiConnected && !isBTConnected){  //若兩者未連線
                if(s.isChecked()){ //BT被選
                    connectionMethod = "Bluetooth";
                    Toast.makeText(getApplicationContext(), R.string.Change_Connection_Method_BT,
                            Toast.LENGTH_SHORT).show();
                }else { //WIFI被選
                    connectionMethod = "Wi-Fi";
                    Toast.makeText(getApplicationContext(), R.string.Change_Connection_Method_WiFI,
                            Toast.LENGTH_SHORT).show();
                }
            }else if(isBTConnected){ //若BT已連線
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.stop_bt_connect_to_wifi)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: 11/8/2018 關閉BT連線，並連接至WIFI
                                //TODO ======================================
                                Toast.makeText(getApplicationContext(), "ERROR. There is no Wi-Fi connection.", Toast.LENGTH_SHORT).show();
                                s.setChecked(true);
                                //TODO ======================================
                            }})
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

            }else if(isWiFiConnected){ //若WIFI已連線 //目前無執行
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.stop_wifi_connect_to_bt)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: 11/8/2018 關閉WIFI連線，並連接至BT
                            }})
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
        if(mBluetoothAdapter != null){
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
    public void Connect (View v){
        //btnConnect.setVisibility(View.INVISIBLE);
        //txConnectStat.setVisibility(View.INVISIBLE);
        //imageConnectStat.setVisibility(View.INVISIBLE);

        if(connectionMethod == "Bluetooth") {

            setBluetoothEnable(true);
            final String address = "98:D3:33:81:25:60"; //HC05的address
            final String name = "SBLUE";

            if(!btAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Please try again after bluetooth enabled.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            //todo progressDialog
            Toast.makeText(getApplicationContext(), R.string.connecting_with_dots, Toast.LENGTH_SHORT).show();
            txConnectStat.setText(R.string.connecting_with_dots);
            imageConnectStat.setVisibility(View.INVISIBLE);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
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
                    // Establish the Bluetooth socket connection.


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
                    if(!fail) {
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
                        imageConnectStat.setVisibility(View.INVISIBLE);
                        FunctionSetEnable(true);

                    }
                }
            }.start();
        }else if(connectionMethod == "Wi-Fi"){
            Toast.makeText(getBaseContext(), "Unavailable",
                    Toast.LENGTH_LONG).show();
        }
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

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        btHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }
    }


    private static class OuterHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public OuterHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                // do something...
                if(msg.what == MESSAGE_READ){
                    try{
                        String readMessage = new String((byte[]) msg.obj, "UTF-8");
                        btDataString.append(readMessage);
                    }catch (UnsupportedEncodingException uee){
                        uee.printStackTrace();
                    }
                    int endOfLineIndex = btDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        //tvSB.setText(String.value)
                        if (btDataString.charAt(0) == '#') {
                            String a = btDataString.substring(1, 3);
                        }
                        btDataString.delete(0, btDataString.length());
                    }
                }
                if (btConnectedThread != null){
                    String sendData = "ab";
                    btConnectedThread.write(sendData);

                }
            }
        }
    }

    private void Disconnect() {

        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (Exception e) {
            }
            btSocket = null;
        }

    }

    //auto按鈕的onClickListener
    //因為switch-case有問題 所以用最原始的方法
    public void auto1(View view) {
        if (!AutoOn1){
            AutoOn1 = true;
            btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto1.setTextColor(getResources().getColor(R.color.white));
            swSk1.setEnabled(false);
        }else{
            AutoOn1 = false;
            btnSkAuto1.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto1.setTextColor(getResources().getColor(R.color.colorPrimary));
            swSk1.setEnabled(true);
        }
    }
    public void auto2(View view) {
        if (!AutoOn2){
            AutoOn2 = true;
            btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto2.setTextColor(getResources().getColor(R.color.white));
            swSk2.setEnabled(false);
        }else{
            AutoOn2 = false;
            btnSkAuto2.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto2.setTextColor(getResources().getColor(R.color.colorPrimary));
            swSk2.setEnabled(true);
        }
    }
    public void auto3(View view) {
        if (!AutoOn3){
            AutoOn3 = true;
            btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto3.setTextColor(getResources().getColor(R.color.white));
            swSk3.setEnabled(false);
        }else{
            AutoOn3 = false;
            btnSkAuto3.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto3.setTextColor(getResources().getColor(R.color.colorPrimary));
            swSk3.setEnabled(true);
        }
    }
    public void auto4(View view) {
        if (!AutoOn4){
            AutoOn4 = true;
            btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto_on));
            btnSkAuto4.setTextColor(getResources().getColor(R.color.white));
            swSk4.setEnabled(false);

        }else{
            AutoOn4 = false;
            btnSkAuto4.setBackground(getResources().getDrawable(R.drawable.button_auto));
            btnSkAuto4.setTextColor(getResources().getColor(R.color.colorPrimary));
            swSk4.setEnabled(true);
        }
    }

    //倒數計時器
    CountDownTimer TimeCountDown  = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long i = millisUntilFinished;
            String b;
            while (i > 60000)
            {
                i = i - 60000;
            }
            if (i < 10000){ b = "0"; }else {b = "";}
            if(AutoOn1){btnSkAuto1.setText(millisUntilFinished/60000 + ":" + b +i/1000);}
            else {btnSkAuto1.setText("AUTO");}
            if(AutoOn2){btnSkAuto2.setText(millisUntilFinished/60000 + ":" + b +i/1000);}
            else {btnSkAuto2.setText("AUTO");}
            if(AutoOn3){btnSkAuto3.setText(millisUntilFinished/60000 + ":" + b +i/1000);}
            else {btnSkAuto3.setText("AUTO");}
            if(AutoOn4){btnSkAuto4.setText(millisUntilFinished/60000 + ":" + b +i/1000);}
            else {btnSkAuto4.setText("AUTO");}
        }

        @Override
        public void onFinish() {
            CustomizedSnackBar("時間到");
            if(AutoOn1){
                btnSkAuto1.setText("AUTO");
                swSk1.setChecked(false);
                btnSkStat1.setImageResource(R.drawable.dot_black_48dp);
            }
            if(AutoOn2){
                btnSkAuto2.setText("AUTO");
                swSk2.setChecked(false);
                btnSkStat2.setImageResource(R.drawable.dot_black_48dp);
            }
            if(AutoOn3){
                btnSkAuto3.setText("AUTO");
                swSk3.setChecked(false);
                btnSkStat3.setImageResource(R.drawable.dot_black_48dp);
            }
            if(AutoOn4){
                btnSkAuto4.setText("AUTO");
                swSk4.setChecked(false);
                btnSkStat4.setImageResource(R.drawable.dot_black_48dp);
            }
        }
    };

    //不能運作 除非使用wait for result
    public void CustomizedAlertDialog(String alertDialogTitle, String alertDialogMessage, String alertDialogPositive, String alertDialogNegative){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(alertDialogTitle)
                .setMessage(alertDialogMessage)
                .setPositiveButton(alertDialogPositive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmSwitch = true;
                    }})
                .setNegativeButton(alertDialogNegative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmSwitch = false;
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        confirmSwitch = false;
                    }
                })
                .show();
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

    public boolean onPrepareOptionsMenu(Menu menu){

        if(!devMode){
            menu.findItem(R.id.action_bt).setVisible(false);
            menu.findItem(R.id.action_auto).setVisible(false);
            menu.findItem(R.id.action_notification).setVisible(false);
            menu.findItem(R.id.action_destroy).setVisible(false);
        }else {
            menu.findItem(R.id.action_bt).setVisible(true);
            menu.findItem(R.id.action_auto).setVisible(true);
            menu.findItem(R.id.action_notification).setVisible(true);
            menu.findItem(R.id.action_destroy).setVisible(true);

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
                makeOreoNotification();
                break;
            case R.id.action_auto:
                TimeCountDown.start();
                break;
            case R.id.action_dev:
                item.setChecked(!item.isChecked());
                devMode = item.isChecked();
                if(!isWiFiConnected && !isBTConnected){ FunctionSetEnable(item.isChecked()); }
                break;
            case  R.id.action_destroy:
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
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
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
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
                        .setSmallIcon(R.drawable.icon_notification_home2)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationTitle)
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        Disconnect();
    }
}


