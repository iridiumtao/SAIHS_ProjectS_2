package app.jerry960331.saihs_projects_2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private Button btnLegacySocketSwitch1, btnLegacySocketSwitch2, btnLegacySocketSwitch3, btnLegacySocketSwitch4,
            btnBTSw;

    String btnLegacySocketSwText1 ,  btnLegacySocketSwText2,  btnLegacySocketSwText3 , btnLegacySocketSwText4;
    private BluetoothAdapter BTAdapter;
    private Set<BluetoothDevice> pairedDevices;

//alt+enter 字串抽離

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //內建fab
        /*
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

    private void findViews() {
        btnLegacySocketSwitch1 = findViewById(R.id.btnLegacySocketSwitch1);
        btnLegacySocketSwitch2 = findViewById(R.id.btnLegacySocketSwitch2);
        btnLegacySocketSwitch3 = findViewById(R.id.btnLegacySocketSwitch3);
        btnLegacySocketSwitch4 = findViewById(R.id.btnLegacySocketSwitch4);
        btnBTSw = findViewById(R.id.btnBTSw);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //onClick
    public void SocketSW1(View view){
        btnLegacySocketSwText1 = btnLegacySocketSwitch1.getText().toString();
        if (getString(R.string.OFF).equals(btnLegacySocketSwText1))
        {
            //try{
            // }catch ()
            // {
            // }

            btnLegacySocketSwitch1.setText(R.string.ON);
        }
        else if(getString(R.string.ON).equals(btnLegacySocketSwText1))
        {
            btnLegacySocketSwitch1.setText(R.string.OFF);
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

}
