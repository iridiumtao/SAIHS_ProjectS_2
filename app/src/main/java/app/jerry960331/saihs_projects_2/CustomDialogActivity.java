package app.jerry960331.saihs_projects_2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CustomDialogActivity extends Dialog implements View.OnClickListener {
    Activity c;
    String functionSelect, currentStat;
    int socketSelect;
    int currentNow;
    Double currentAve;
    boolean isSWOn;
    private TextView txCurrentStat, txCurrentNow, txCurrentAve, txCurrentDescription;
    private ImageView imageCurrentStat;
    private SimpleLineChart mSimpleLineChart;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }
    int timeSet;
    private long timeCountInMilliSeconds = 1 * 60000;
    private ProgressBar progressBarCircle;
    private EditText editTextMinute;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;
    private TimerStatus timerStatus = TimerStatus.STOPPED;


    String[] xChart = {};
    String[] yChart = {};
    int[] currentValue = {};


    CustomDialogActivity(Activity a){
        super(a);
        c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        editTextMinute = (EditText) findViewById(R.id.editTextMinute);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
        imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);



        switch (functionSelect){
            case "Stat":
                Log.d("d","Stat");
                setContentView(R.layout.current_dialog);

                txCurrentStat = findViewById(R.id.txCurrentStat);
                txCurrentNow = findViewById(R.id.txCurrentNow);
                txCurrentAve = findViewById(R.id.txCurrentAve);
                txCurrentDescription = findViewById(R.id.txCurrentDescription);
                imageCurrentStat = findViewById(R.id.imageCurrentStat);

                txCurrentStat.setText(currentStat);
                txCurrentNow.setText(currentNow+" mA");
                txCurrentAve.setText(currentAve+" mA");

                if (currentNow == 0) {
                    txCurrentStat.setText(R.string.socket_off);
                    txCurrentDescription.setText(R.string.current_description_off);
                    imageCurrentStat.setImageResource(R.drawable.dot_black_48dp);
                }else if (currentNow > 0 && currentNow < 8000){
                    txCurrentStat.setText(R.string.good);
                    txCurrentDescription.setText(R.string.current_description_good);
                    imageCurrentStat.setImageResource(R.drawable.dot_green_48dp);
                }else if (currentNow > 8000 && currentNow < 15000) {
                    txCurrentStat.setText(R.string.orange);
                    txCurrentDescription.setText(R.string.current_description_orange);
                    imageCurrentStat.setImageResource(R.drawable.dot_orange_48dp);
                }else if (currentNow > 15000) {
                    txCurrentStat.setText(R.string.red);
                    txCurrentDescription.setText(R.string.current_description_red);
                    imageCurrentStat.setImageResource(R.drawable.dot_red_48dp);
                }else {
                    txCurrentStat.setText(R.string.socket_off);
                    txCurrentDescription.setText(R.string.current_description_off);
                    imageCurrentStat.setImageResource(R.drawable.dot_black_48dp);
                }
                break;
            case "Alarm": //todo==============================================
                setContentView(R.layout.alarm_dialog);
                imageViewStartStop.setOnClickListener(SetTimer);
                switch (socketSelect){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
                break;
            case "Chart":
                Log.d("d","chart");
                setContentView(R.layout.chart_dialog);
                mSimpleLineChart = (SimpleLineChart) findViewById(R.id.simpleLineChart);
                mSimpleLineChart.setXItem(xChart);
                mSimpleLineChart.setYItem(yChart);
                HashMap<Integer,Integer> pointMap = new HashMap();
                for(int i = 0;i<xChart.length;i++){
                    pointMap.put(i,currentValue[i]);
                }
                mSimpleLineChart.setData(pointMap);
                break;
        }
    }


    private ImageView.OnClickListener SetTimer = new ImageView.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (!editTextMinute.getText().toString().isEmpty()) {
                timeSet = Integer.parseInt(editTextMinute.getText().toString().trim());
            }
            timeCountInMilliSeconds = timeSet * 60 * 1000;

            if (timerStatus == TimerStatus.STOPPED) {
                setProgressBarValues();
                imageViewReset.setVisibility(View.VISIBLE);
                imageViewStartStop.setImageResource(R.drawable.icon_stop);
                editTextMinute.setEnabled(false);
                timerStatus = TimerStatus.STARTED;
                DialogTimer.start();
            } else {
                imageViewReset.setVisibility(View.GONE);
                imageViewStartStop.setImageResource(R.drawable.icon_start);
                editTextMinute.setEnabled(true);
                timerStatus = TimerStatus.STOPPED;
                DialogTimer.cancel();
            }



        }
    };
    CountDownTimer[] timer = new CountDownTimer[4];

    private CountDownTimer DialogTimer = new CountDownTimer(timeCountInMilliSeconds,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
            progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
            // call to initialize the progress bar values
            setProgressBarValues();
            // hiding the reset icon
            imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            imageViewStartStop.setImageResource(R.drawable.icon_start);
            // making edit text editable
            editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = CustomDialogActivity.TimerStatus.STOPPED;
        }
    };


    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private String hmsTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }

    @Override
    public void onClick(View v) {

    }


}
