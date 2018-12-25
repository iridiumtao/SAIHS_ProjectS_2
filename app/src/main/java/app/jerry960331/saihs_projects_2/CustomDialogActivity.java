package app.jerry960331.saihs_projects_2;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CustomDialogActivity extends Dialog implements View.OnClickListener {
    Activity c;
    String functionSelect, currentStat;
    int socketSelect;
    int currentNow;
    int powerNow;
    Double currentAve;
    boolean isSWOn;
    private TextView txCurrentStat, txCurrentNow, txPowerNow, txCurrentAve, txCurrentDescription;
    private ImageView imageCurrentStat;
    private SimpleLineChart mSimpleLineChart;


    Button btnGotoTimer;
    TextView txNowTime;
    TextView txNowDate;
    ImageButton btnAlarmIsOn1;
    TextView txAlarmSetSchedule1;
    TextView txAlarmSetTime1;
    TextView txAlarmIntent1;
    FloatingActionButton fabAlarm;
    boolean isAlarmOn1;
    OnMyDialogResult mDialogResult; //回傳鬧鐘資料
    LinearLayout alarmSet1;


    private int timeSet;
    private long timeCountInMilliSeconds = 1 * 60000;
    private ProgressBar progressBarCircle;
    private EditText editTextMinute;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;
    private boolean timerOn1 = false, timerOn2 = false, timerOn3 = false, timerOn4 = false;

    Long remainTime = (long) 0;


    String[] xChart = {};
    String[] yChart = {};
    int[] currentValue = {};


    CustomDialogActivity(Activity a) {
        super(a);
        c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CustomDialogActivity:", "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //sectionPageAdapter = new SectionPageAdapter;

        switch (functionSelect) {
            case "Stat":

                setContentView(R.layout.current_dialog);

                //final MainActivity MainActivity = new MainActivity(getOwnerActivity());
                //MainActivity.fuck = 45;

                txCurrentStat = findViewById(R.id.txCurrentStat);
                txCurrentNow = findViewById(R.id.txCurrentNow);
                txCurrentAve = findViewById(R.id.txCurrentAve);
                txCurrentDescription = findViewById(R.id.txCurrentDescription);
                imageCurrentStat = findViewById(R.id.imageCurrentStat);
                txPowerNow = findViewById(R.id.txPowerNow);


                txCurrentStat.setText(currentStat);
                txCurrentNow.setText(currentNow + " mA");
                txCurrentAve.setText(currentAve + " mA");
                txPowerNow.setText(currentNow * 0.11 + " W");


                if (currentNow == 0) {
                    txCurrentStat.setText(R.string.socket_off);
                    txCurrentDescription.setText(R.string.current_description_off);
                    imageCurrentStat.setImageResource(R.drawable.dot_black_48dp);
                } else if (currentNow > 0 && currentNow < 3000) {
                    txCurrentStat.setText(R.string.good);
                    txCurrentDescription.setText(R.string.current_description_good);
                    imageCurrentStat.setImageResource(R.drawable.dot_green_48dp);
                    //}else if (currentNow > 8000 && currentNow < 12000) {
                    //  txCurrentStat.setText(R.string.orange);
                    //  txCurrentDescription.setText(R.string.current_description_orange);
                    //  imageCurrentStat.setImageResource(R.drawable.dot_orange_48dp);
                } else if (currentNow > 3000) {
                    txCurrentStat.setText(R.string.red);
                    txCurrentDescription.setText(R.string.current_description_red);
                    imageCurrentStat.setImageResource(R.drawable.dot_red_48dp);


                } else {
                    txCurrentStat.setText(R.string.socket_off);
                    txCurrentDescription.setText(R.string.current_description_off);
                    imageCurrentStat.setImageResource(R.drawable.dot_black_48dp);
                }
                break;
            case "Alarm": //todo==============================================
                setContentView(R.layout.alarm_dialog);
                setTitle("Alarm");


                btnGotoTimer = findViewById(R.id.btnGotoTimer);
                txNowTime = findViewById(R.id.txNowTime);
                txNowDate = findViewById(R.id.txNowDate);
                btnAlarmIsOn1 = findViewById(R.id.btnAlarmIsOn1);
                btnAlarmIsOn1.setOnClickListener(AlarmIsOnOnClick1);
                txAlarmSetSchedule1 = findViewById(R.id.txAlarmSetSchedule1);
                txAlarmSetTime1 = findViewById(R.id.txAlarmSetTime1);
                txAlarmIntent1 = findViewById(R.id.txAlarmIntent1);
                fabAlarm = findViewById(R.id.fabAlarm);
                alarmSet1 = findViewById(R.id.alarmSet1);

                if (isAlarmOn1){
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_on);
                    isAlarmOn1 = true;
                    Toast.makeText(getContext(),"XD",Toast.LENGTH_SHORT).show();
                }else {
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                    isAlarmOn1 = false;
                }

                /*
                progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
                editTextMinute = (EditText) findViewById(R.id.editTextMinute);
                textViewTime = (TextView) findViewById(R.id.textViewTime);
                imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
                imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
                imageViewStartStop.setOnClickListener(SetTimer);
                */
                break;
            case "Chart":
                Log.d("d", "chart");
                setContentView(R.layout.chart_dialog);
                mSimpleLineChart = (SimpleLineChart) findViewById(R.id.simpleLineChart);
                mSimpleLineChart.setXItem(xChart);
                mSimpleLineChart.setYItem(yChart);
                HashMap<Integer, Integer> pointMap = new HashMap();
                for (int i = 0; i < xChart.length; i++) {
                    pointMap.put(i, currentValue[i]);
                }
                mSimpleLineChart.setData(pointMap);
                break;
        }

        alarmSet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txAlarmSetTime1.setText(hourOfDay  +":" + minute);
                    }
                }, hour, minute, false);

                timePickerDialog.show();
            }
        });
    }


    private ImageView.OnClickListener SetTimer = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!editTextMinute.getText().toString().isEmpty()) {
                timeSet = Integer.parseInt(editTextMinute.getText().toString().trim());
            }
            timeCountInMilliSeconds = timeSet * 60 * 1000;


            switch (socketSelect) {
                case 1:
                    if (!timerOn1) {
                        setProgressBarValues();
                        imageViewReset.setVisibility(View.VISIBLE);
                        imageViewStartStop.setImageResource(R.drawable.icon_stop);
                        editTextMinute.setEnabled(false);
                        //main.DialogTimer.start();
                        timerOn1 = true;
                    } else {
                        imageViewReset.setVisibility(View.GONE);
                        imageViewStartStop.setImageResource(R.drawable.icon_start);
                        editTextMinute.setEnabled(true);
                        timerOn1 = false;
                    }
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
        }
    };

    private ImageButton.OnClickListener AlarmIsOnOnClick1 = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isAlarmOn1){
                btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                isAlarmOn1 = false;
            }else {
                btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_on);
                isAlarmOn1 = true;
            }
        }
    };

    class AlarmAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }



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
    protected void onStop() {
        Log.d("CustomDialogActivity:", "onStop");
        mDialogResult.finish("FUCK");
        mDialogResult.isAlarmOn1(isAlarmOn1);
        mDialogResult.alarmSetTime1("");
        mDialogResult.alarmSetSchedule1("");
        mDialogResult.alarmIntent1("");
        super.onStop();


    }
    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
        void isAlarmOn1(Boolean b);
        void alarmSetTime1(String hhmm);
        void alarmSetSchedule1(String schedule);
        void alarmIntent1(String function);
    }

    @Override
    public void onClick(View v) {

    }
}

