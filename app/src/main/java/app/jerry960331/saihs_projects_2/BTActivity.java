package app.jerry960331.saihs_projects_2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class BTActivity extends AppCompatActivity {

    private TextView mBTStatTxV;
    private TextView mRXTxV;
    private Button mBTOnBtn;
    private Button mBTOffBtn;
    private Button mShowPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private EditText mBTEditText;
    private Button mBTSendBtn;
    private TextView mSentMsgTxV;
    private Handler mHandler;
    // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread;
    // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null;
    // bi-directional client-to-client data path
    private static final UUID BTMODULEUUID = UUID.fromString
            ("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1;
    // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2;
    // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3;
    // used in bluetooth handler to identify message status
    private  String _receiveData = "";
    private BluetoothSocket mmSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Bluetooth Developer");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        findViews();
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevicesListView.setAdapter(mBTArrayAdapter);
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        //詢問藍芽位置權限
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION},1);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try{
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        readMessage = readMessage.substring(0,1);
                        _receiveData += readMessage;
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    mRXTxV.setText(_receiveData);
                }
                 if (msg.what == CONNECTING_STATUS){
                    if (msg.arg1 == 1)
                        mBTStatTxV.setText("Connected to Device:"+ (String)(msg.obj));
                    else
                        mBTStatTxV.setText("Connection Failed");
                 }
            }

        };

        if (mBTArrayAdapter == null){
            mBTStatTxV.setText("Bluetooth not found");

            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {



            mBTSendBtn.setOnClickListener(new View.OnClickListener(){
                //當按下send開始傳輸資料
                @Override
                public void onClick(View v){
                    _receiveData = ""; //清除上次收到的資料
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write(mBTEditText.getText().toString());
                    //傳送將輸入的資料出去
                    mSentMsgTxV.setText(mSentMsgTxV.getText() + "\n" + mBTEditText.getText());

                }
            });

            //定義每個按鍵按下後要做的事情
            mBTOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mBTOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mShowPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });

        }
    }

    private void bluetoothOn(View view){
        try {
            if (!mBTAdapter.isEnabled()) {//如果藍芽沒開啟
                Intent enableBtIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//跳出視窗
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                //開啟設定藍芽畫面
                mBTStatTxV.setText("Bluetooth enabled");
                Toast.makeText(getApplicationContext(), R.string.Bluetooth_tuned_on, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.Bluetooth_is_already_on,
                        Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), R.string.BTCrash,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    //定義當按下跳出是否開啟藍芽視窗後要做的內容
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBTStatTxV.setText("Bluetooth Enabled");
            }
            else
                mBTStatTxV.setText("Bluetooth Disabled");
        }
    }

    private void bluetoothOff(View view){
        try {
            mBTAdapter.disable(); // turn off bluetooth
            mBTStatTxV.setText("Bluetooth Disabled");
            Toast.makeText(getApplicationContext(), "Bluetooth turned Off",
                    Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(), R.string.BTCrash,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void discover(View view){
        try{
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){ //如果已經找到裝置
            mBTAdapter.cancelDiscovery(); //取消尋找
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) { //如果沒找到裝置且已按下尋找
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery(); //開始尋找
                Toast.makeText(getApplicationContext(), "Discovery started",
                        Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new
                        IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on",
                        Toast.LENGTH_SHORT).show();
            }
        }
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), R.string.BTCrash,
                Toast.LENGTH_SHORT).show();
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        try {
            mPairedDevices = mBTAdapter.getBondedDevices();
            if (mBTAdapter.isEnabled()) {
                // put it's one to the adapter
                for (BluetoothDevice device : mPairedDevices)
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                Toast.makeText(getApplicationContext(), "Show Paired Devices",
                        Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Bluetooth not on",
                        Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {Toast.makeText(getApplicationContext(), R.string.BTCrash,
                Toast.LENGTH_SHORT).show();}
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new
            AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

                    if(!mBTAdapter.isEnabled()) {
                        Toast.makeText(getBaseContext(), "Bluetooth not on",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mBTStatTxV.setText("Connecting...");
                    // Get the device MAC address, which is the last 17 chars in the View
                    String info = ((TextView) v).getText().toString();
                    final String address = info.substring(info.length() - 17);
                    final String name = info.substring(0,info.length() - 17);

                    // Spawn a new thread to avoid blocking the GUI one
                    new Thread()
                    {
                        public void run() {
                            boolean fail = false;
                            //取得裝置MAC找到連接的藍芽裝置
                            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                            try {
                                mBTSocket = createBluetoothSocket(device);
                                //建立藍芽socket
                            } catch (IOException e) {
                                fail = true;
                                Toast.makeText(getBaseContext(), "Socket creation failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // Establish the Bluetooth socket connection.
                            try {
                                mBTSocket.connect(); //建立藍芽連線
                            } catch (IOException e) {
                                try {
                                    fail = true;
                                    mBTSocket.close(); //關閉socket
                                    //開啟執行緒 顯示訊息
                                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                            .sendToTarget();
                                } catch (IOException e2) {
                                    //insert code to deal with this
                                    Toast.makeText(getBaseContext(), "Socket creation failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(!fail) {
                                //開啟執行緒用於傳輸及接收資料
                                mConnectedThread = new ConnectedThread(mBTSocket);
                                mConnectedThread.start();
                                //開啟新執行緒顯示連接裝置名稱
                                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                        .sendToTarget();
                            }
                        }
                    }.start();
                }
            };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws
            IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread {

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
            } catch (IOException e) { }

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
                    if(bytes != 0) {
                        SystemClock.sleep(100);
                        //pause and wait for rest of data
                        bytes = mmInStream.available();
                        // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes);
                        // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */

    }

    public void cancel() {
        if (mmSocket!= null){
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



    private void findViews() {
        mBTStatTxV = findViewById(R.id.BTStatTxV);
        mRXTxV = findViewById(R.id.BTRXTxV);
        mBTOnBtn = findViewById(R.id.BTOnBtn);
        mBTOffBtn = findViewById(R.id.BTOffBtn);
        mDiscoverBtn = findViewById(R.id.BTDiscoverBtn);
        mShowPairedDevicesBtn = findViewById(R.id.BTShowPairedBtn);
        mBTEditText = findViewById(R.id.BTEditText);
        mBTSendBtn = findViewById(R.id.BTSendBtn);
        mDevicesListView = findViewById(R.id.devicesListView);
        mSentMsgTxV = findViewById(R.id.sentMsgTxV);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();


        cancel();
    }

}
