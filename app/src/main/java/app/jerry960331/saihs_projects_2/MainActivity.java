package app.jerry960331.saihs_projects_2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    private ImageButton
            btnSkStat1, btnSkStat2, btnSkStat3, btnSkStat4,
            btnSkAlarm1, btnSkAlarm2, btnSkAlarm3, btnSkAlarm4,
            btnSkChart1, btnSkChart2, btnSkChart3, btnSkChart4;

    private Switch
            swSk1,
            swSk2,
            swSk3,
            swSk4;


    //color
    //may be added to color.xml
    public static  int red = 0xfff44336;
    public static  int green = 0xff4caf50;
    public static  int blue = 0xff2195f3;
    public static  int orange = 0xffffc107;

    //snackbar customize
    private Snackbar snackbar;
    private View snackBarView;
    private TextView txVStat,snackBarTxV;


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

        txVStat.bringToFront();

    }
//ctrl+alt+M
    private void findViews() {
        swSk1 = findViewById(R.id.swSk1);
        swSk2 = findViewById(R.id.swSk2);
        swSk3 = findViewById(R.id.swSk3);
        swSk4 = findViewById(R.id.swSk4);

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

    private Switch.OnClickListener SwListener = new Switch.OnClickListener(){
        @Override
        public void onClick(View v){
            final Switch t = (Switch)v;
            String switchText = t.getText().toString();
            final int switchId = t.getId();
            String switchOnOff;
            if (t.isChecked()) {
                switchOnOff = getResources().getString(R.string.open);
            }
            else {
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
                            switch (switchId){
                                case R.id.swSk1:
                                    if(t.isChecked()) {
                                        btnSkStat1.setImageResource(R.drawable.dot_green_48dp);
                                        i = 1;
                                        IO = getResources().getString(R.string.turnOn);
                                    }
                                    else{
                                        btnSkStat1.setImageResource(R.drawable.dot_black_48dp);
                                        i = 1;
                                        IO = getResources().getString(R.string.turnOff);
                                    }
                                    break;
                                case R.id.swSk2:
                                    if(t.isChecked()) {
                                        btnSkStat2.setImageResource(R.drawable.dot_green_48dp);
                                        i = 2;
                                        IO = getResources().getString(R.string.turnOn);
                                    }
                                    else{
                                        btnSkStat2.setImageResource(R.drawable.dot_black_48dp);
                                        i = 2;
                                        IO = getResources().getString(R.string.turnOff);
                                    }
                                    break;
                                case R.id.swSk3:
                                    if(t.isChecked()) {
                                        btnSkStat3.setImageResource(R.drawable.dot_green_48dp);
                                        IO = getResources().getString(R.string.turnOn);
                                        i = 3;
                                    }
                                    else{
                                        btnSkStat3.setImageResource(R.drawable.dot_black_48dp);
                                        i = 3;
                                        IO = getResources().getString(R.string.turnOff);

                                    }
                                    break;
                                case  R.id.swSk4:
                                    if(t.isChecked()) {
                                        btnSkStat4.setImageResource(R.drawable.dot_green_48dp);
                                        i = 4;
                                        IO = getResources().getString(R.string.turnOn);
                                    }
                                    else{
                                        btnSkStat4.setImageResource(R.drawable.dot_black_48dp);
                                        i = 4;
                                        IO = getResources().getString(R.string.turnOff);

                                    }
                                    break;
                            }
                            SnackbarText = getResources().getString(R.string.socket) + " " + i + " " + IO;
                            snackbar = Snackbar.make(findViewById(android.R.id.content), SnackbarText, Snackbar.LENGTH_SHORT)
                                    .setAction("DISMISS",null);

                            snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(blue);
                            snackBarTxV = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);

                            snackbar.show();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(t.isChecked()) {
                                t.setChecked(false);
                            }
                            else{
                                t.setChecked(true);
                            }
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if(t.isChecked()) {
                                t.setChecked(false);
                            }
                            else{
                                t.setChecked(true);
                            }
                        }
                    })
                    .show();
        }
    };

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

        switch (id)
        {
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
                        .setNegativeButton(getResources().getString(R.string.cancel),null)
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
                        .setNegativeButton(getResources().getString(R.string.cancel),null)
                        .show();
                break;
            case  R.id.action_help:


        }

        /**
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
         */

        return super.onOptionsItemSelected(item);
    }
}
