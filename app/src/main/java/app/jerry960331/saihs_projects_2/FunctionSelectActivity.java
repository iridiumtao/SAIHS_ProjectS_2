package app.jerry960331.saihs_projects_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FunctionSelectActivity extends AppCompatActivity {
    ImageView imgBS, imgSO, imgInfrared, imgEnvi;
    Button btnBSView, btnBSSetting, btnSOView, btnSOSetting,
            btnInfraredView, btnInfraredSetting, btnEnviView, btnEnviSetting;
    TextView txBS, txSO, txInfrared, txEnvi,
            txCO, txCH4, txTemp, txHumidity;
    Menu menu;
    boolean showFSA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFSA = getSharedPreferences("user", MODE_PRIVATE).getBoolean("showFSA", true);
        if (!showFSA){
            Intent intent = new Intent(FunctionSelectActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_func);
        findViews();
        onClickListeners();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("歐東的家");
        txBS.setText(getSharedPreferences("user", MODE_PRIVATE).getString("user_device_BS", "Blue Storm III"));
        txSO.setText(getSharedPreferences("user", MODE_PRIVATE).getString("user_device_SO", "Salvation October"));


        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothFragment fragment = new BluetoothFragment();
            transaction.replace(R.id.bluetoothFragmentLayout, fragment);
            transaction.commit();
        }
    }

    private void findViews() {
        imgBS = findViewById(R.id.imgBS);
        imgSO = findViewById(R.id.imgSO);
        imgInfrared = findViewById(R.id.imgInfrared);
        imgEnvi = findViewById(R.id.imgEnvi);

        btnBSView = findViewById(R.id.btnBSView);
        btnSOView = findViewById(R.id.btnSOView);
        btnInfraredView = findViewById(R.id.btnInfraredView);
        btnEnviView = findViewById(R.id.btnEnviView);

        btnBSSetting = findViewById(R.id.btnBSSetting);
        btnSOSetting= findViewById(R.id.btnSOSetting);
        btnInfraredSetting = findViewById(R.id.btnInfraredSetting);
        btnEnviSetting = findViewById(R.id.btnEnviSetting);

        txBS = findViewById(R.id.txBS);
        txSO = findViewById(R.id.txSO);
        txInfrared = findViewById(R.id.txInfrared);
        txEnvi = findViewById(R.id.txEnvi);

        txCO = findViewById(R.id.txCO);
        txCH4 = findViewById(R.id.txCH4);
        txTemp = findViewById(R.id.txTemp);
        txHumidity = findViewById(R.id.txHumidity);
    }
    private void onClickListeners() {
        btnBSView.setOnClickListener(btnBSView_onClick);
        btnSOView.setOnClickListener(btnSOView_onClick);
        btnInfraredView.setOnClickListener(btnInfraredView_onClick);
        btnEnviView.setOnClickListener(btnEnviView_onClick);

        btnBSSetting.setOnClickListener(btnBSSetting_onClick);
        btnSOSetting.setOnClickListener(btnSOSetting_onClick);
        btnInfraredSetting.setOnClickListener(btnInfraredSetting_onClick);
        btnEnviSetting.setOnClickListener(btnEnviSetting_onClick);
    }

    private Button.OnClickListener btnBSView_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FunctionSelectActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
    private Button.OnClickListener btnSOView_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FunctionSelectActivity.this, SOActivity.class);
            startActivity(intent);
        }
    };
    private Button.OnClickListener btnInfraredView_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };
    private Button.OnClickListener btnEnviView_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };

    private Button.OnClickListener btnBSSetting_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };
    private Button.OnClickListener btnSOSetting_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };
    private Button.OnClickListener btnInfraredSetting_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };
    private Button.OnClickListener btnEnviSetting_onClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }



}
