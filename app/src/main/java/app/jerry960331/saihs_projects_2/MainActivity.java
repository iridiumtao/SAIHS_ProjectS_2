package app.jerry960331.saihs_projects_2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private Button
            btnLegacySocketSwitch1,
            btnLegacySocketSwitch2,
            btnLegacySocketSwitch3,
            btnLegacySocketSwitch4,
            btnBTSw;

    private String
            btnLegacySocketSwText1 ,
            btnLegacySocketSwText2,
            btnLegacySocketSwText3 ,
            btnLegacySocketSwText4;





    //color
    //may be added to color.xml
    public static  int red = 0xfff44336;
    public static  int green = 0xff4caf50;
    public static  int blue = 0xff2195f3;
    public static  int orange = 0xffffc107;

    //snackbar customize
    private Snackbar snackbar;
    private View snackBarView;
    private TextView snackBarTxV;


//alt+enter 字串抽離

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Smart Socket");

        //內建fab
        /**
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


        findViews();

        btnLegacySocketSwitch1.setText(R.string.OFF);
        btnLegacySocketSwitch2.setText(R.string.OFF);
        btnLegacySocketSwitch3.setText(R.string.OFF);
        btnLegacySocketSwitch4.setText(R.string.OFF);








    }
//ctrl+alt+M
    private void findViews() {
        btnLegacySocketSwitch1 = findViewById(R.id.btnLegacySocketSwitch1);
        btnLegacySocketSwitch2 = findViewById(R.id.btnLegacySocketSwitch2);
        btnLegacySocketSwitch3 = findViewById(R.id.btnLegacySocketSwitch3);
        btnLegacySocketSwitch4 = findViewById(R.id.btnLegacySocketSwitch4);
        btnBTSw = findViewById(R.id.btnBTSw);

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

        switch (id)
        {
            case R.id.action_settings:
                Intent intentSetting = new Intent(this, SettingsActivity.class);
                startActivity(intentSetting);
                break;
            case R.id.action_bt:
                Intent intentBT = new Intent(this, BTActivity.class);
                startActivity(intentBT);
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


    //onClick
    public void SocketSW1(View view){
        btnLegacySocketSwText1 = btnLegacySocketSwitch1.getText().toString();
        if (getString(R.string.OFF).equals(btnLegacySocketSwText1))
        {
            /**
             * try{
            * }catch ()
            * {
            * }
             * */

            btnLegacySocketSwitch1.setText(R.string.ON);
            Snackbar.make(view,"Set socket 1 ON", Snackbar.LENGTH_LONG)
                    .setAction("DISMISS", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this,"你点击了action",Toast.LENGTH_SHORT).show();
                }
            }).show();
        }
        else if(getString(R.string.ON).equals(btnLegacySocketSwText1))
        {
            btnLegacySocketSwitch1.setText(R.string.OFF);

            Snackbar.make(view, "Set socket 1 OFF", Snackbar.LENGTH_LONG)
                    .setAction("DISMISS", new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
        }
    }


    public void SocketSW2(View view){
        btnLegacySocketSwText2 = btnLegacySocketSwitch2.getText().toString();
        if (getString(R.string.OFF).equals(btnLegacySocketSwText2))
        {
            //try{
            // }catch ()
            // {
            // }

            btnLegacySocketSwitch2.setText(R.string.ON);


            snackbar = Snackbar.make(view, "Set socket 2 ON", Snackbar.LENGTH_SHORT)
                               .setAction("DISMISS",null);

            snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(blue);
            snackBarTxV = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            /* snackBarTxV.setTextColor(red); */
            snackbar.show();

        }
        else if(getString(R.string.ON).equals(btnLegacySocketSwText2))
        {
            btnLegacySocketSwitch2.setText(R.string.OFF);
        }

    }

    public void SocketSW3(View view){
        btnLegacySocketSwText3 = btnLegacySocketSwitch3.getText().toString();
        if (getString(R.string.OFF).equals(btnLegacySocketSwText3))
        {
            //try{
            // }catch ()
            // {
            // }

            btnLegacySocketSwitch3.setText(R.string.ON);
        }
        else if(getString(R.string.ON).equals(btnLegacySocketSwText3))
        {
            btnLegacySocketSwitch3.setText(R.string.OFF);
        }

    }

    public void SocketSW4(View view){
        btnLegacySocketSwText4 = btnLegacySocketSwitch4.getText().toString();
        if (getString(R.string.OFF).equals(btnLegacySocketSwText4))
        {
            //try{
            // }catch ()
            // {
            // }

            btnLegacySocketSwitch4.setText(R.string.ON);
        }
        else if(getString(R.string.ON).equals(btnLegacySocketSwText4))
        {
            btnLegacySocketSwitch4.setText(R.string.OFF);
        }

    }


    public void BTSw(View view){
        Intent intent = new Intent(this, BTActivity.class);
        startActivity(intent);
    }
}
