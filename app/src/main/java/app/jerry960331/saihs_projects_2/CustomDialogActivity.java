package app.jerry960331.saihs_projects_2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.os.Looper.getMainLooper;

public class CustomDialogActivity extends Dialog implements View.OnClickListener {
    Activity c;
    String functionSelect, currentStat;
    int socketSelect;
    int currentNow;
    int powerNow;
    Double currentAve;
    boolean isSWOn;
    TextView txCurrentStat, txCurrentNow, txPowerNow, txCurrentAve, txCurrentDescription;
    ImageView imageCurrentStat;


    Button btnGotoTimer;
    TextView txNowTime;
    TextView txNowDate;
    ImageButton btnAlarmIsOn1;
    TextView txAlarmSetSchedule1;
    TextView txAlarmSetTime1;
    TextView txAlarmIntent1;
    FloatingActionButton fabAlarm;
    boolean isAlarmOn1;
    String alarmSetTime1 = "";
    Calendar alarmCal;
    OnMyDialogResult mDialogResult; //回傳鬧鐘資料
    LinearLayout alarmSet1;
    ArrayList selectedItems = new ArrayList();
    boolean[] checkedItems;


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

    @SuppressLint("SetTextI18n")
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
            case "Alarm": //todo================================================================
                setContentView(R.layout.alarm_dialog);
                setTitle("Alarm");



                btnGotoTimer = findViewById(R.id.btnGotoTimer);
                txNowTime = findViewById(R.id.txNowTime);
                txNowDate = findViewById(R.id.txNowDate);
                btnAlarmIsOn1 = findViewById(R.id.btnAlarmIsOn1);
                txAlarmSetSchedule1 = findViewById(R.id.txAlarmSetSchedule1);
                txAlarmSetTime1 = findViewById(R.id.txAlarmSetTime1);
                txAlarmIntent1 = findViewById(R.id.txAlarmIntent1);
                fabAlarm = findViewById(R.id.fabAlarm);
                alarmSet1 = findViewById(R.id.alarmSet1);
                txAlarmSetSchedule1.setSelected(true);
                final String[] date = {"週一","週二","週三","週四","週五","週六","週日"};



                final Handler clockHandler = new Handler(getMainLooper());
                clockHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txNowTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                        txNowDate.setText(new SimpleDateFormat("MM月dd日 E", Locale.getDefault()).format(new Date()));
                        clockHandler.postDelayed(this, 1000);
                    }
                }, 10);

                //若鬧鐘先前已開啟，則將圖示開啟
                if (isAlarmOn1){
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_on);
                    isAlarmOn1 = true;
                    Toast.makeText(getContext(),"鬧鐘先前設定開啟",Toast.LENGTH_SHORT).show();
                }else {
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                    isAlarmOn1 = false;
                }


                //todo 讓「週期」的字能夠在開啟時跑出來
                //todo 目前暫時在這邊跑，之後希望可以打開的時候去鬧鐘那邊check是不是有設定
                String s = "";
                try {
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            s += date[i] + "、";
                        }
                    }
                    s = s.substring(0, s.length() - 1);
                    txAlarmSetSchedule1.setText(s);
                }catch (Exception ignore){}

                //若未設定週期 則不允許鬧鐘開啟
                if(txAlarmSetSchedule1.getText().toString().equals(getContext().getString(R.string.not_set))) {
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                    isAlarmOn1 = false;
                }

                //如果未設定鬧鐘時間 則預設為目前時間
                if(alarmSetTime1 == ""){
                    txAlarmSetTime1.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                }else {
                    txAlarmSetTime1.setText(alarmSetTime1);
                }


                /*
                progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
                editTextMinute = (EditText) findViewById(R.id.editTextMinute);
                textViewTime = (TextView) findViewById(R.id.textViewTime);
                imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
                imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
                imageViewStartStop.setOnClickListener(SetTimer);
                */

                alarmSet1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int hour = Integer.parseInt(txAlarmSetTime1.getText().toString().substring(0,2));
                        int minute = Integer.parseInt(txAlarmSetTime1.getText().toString().substring(3));

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                txAlarmSetTime1.setText(String.format("%02d:%02d", hourOfDay, minute, Locale.getDefault()));
                            }
                        }, hour, minute, true);

                        timePickerDialog.show();
                    }
                });

                txAlarmSetSchedule1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final AlertDialog.Builder datePickDialog = new AlertDialog.Builder(getContext());
                        datePickDialog.setTitle("請選擇週期");
                        datePickDialog.setNeutralButton("不重複", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               //TODO 按下「不重複」之後隱藏目前Dialog(或只隱藏date ListView)，顯示下一個時間點及一個時間(點擊後開啟DatePicker)
                            }
                           });
                        datePickDialog.setMultiChoiceItems(date, checkedItems, new OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(which);
                                    Toast.makeText(getContext(),which+"",Toast.LENGTH_SHORT).show();

                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(Integer.valueOf(which));
                                    Toast.makeText(getContext(),"-"+which,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        datePickDialog.setPositiveButton(R.string.finish, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = "";

                                try {
                                    for (int i = 0; i < checkedItems.length; i++) {
                                        if (checkedItems[i]) {
                                            s += date[i] + "、";
                                        }
                                    }
                                    s = s.substring(0, s.length() - 1);
                                    Toast.makeText(getContext(), "已設定週期為" + s, Toast.LENGTH_LONG).show();
                                    txAlarmSetSchedule1.setText(s);
                                }catch (Exception e){
                                    Toast.makeText(getContext(), "未完成設定", Toast.LENGTH_LONG).show();
                                    txAlarmSetSchedule1.setText(R.string.not_set);
                                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                                    isAlarmOn1 = false;
                                }
                            }
                        });
                        datePickDialog.setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //我也不知道StringBuilder是啥 按那個燈泡它自己跑出來的
                                StringBuilder s = new StringBuilder();
                                try {
                                    for (int i = 0; i < checkedItems.length; i++) {
                                        if (checkedItems[i]) {
                                            s.append(date[i]).append("、");
                                        }
                                    }
                                    s = new StringBuilder(s.substring(0, s.length() - 1));
                                    Toast.makeText(getContext(), "已設定週期為" + s, Toast.LENGTH_LONG).show();
                                    txAlarmSetSchedule1.setText(s.toString());
                                }catch (Exception e){
                                    Toast.makeText(getContext(), "未完成設定", Toast.LENGTH_LONG).show();
                                    txAlarmSetSchedule1.setText(R.string.not_set);
                                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                                    isAlarmOn1 = false;
                                }
                            }
                        });
                        datePickDialog.show();
                    }
                });

                txAlarmIntent1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo
                    }
                });

                btnAlarmIsOn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txAlarmSetSchedule1.getText().toString().equals(getContext().getString(R.string.not_set))) {
                            btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                            isAlarmOn1 = false;
                            Toast.makeText(getContext(),"未設定週期",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!isAlarmOn1){//turn on
                            //todo 記住這個鬧鐘
                            btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_on);

                            isAlarmOn1 = true;
                        }else {//turn off
                            //todo 取消這個鬧鐘
                            btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                            isAlarmOn1 = false;
                        }

                    }
                });
                break;

            case "Chart":
                Log.d("d", "chart");
                setContentView(R.layout.chart_dialog);
                SimpleLineChart mSimpleLineChart = (SimpleLineChart) findViewById(R.id.simpleLineChart);
                mSimpleLineChart.setXItem(xChart);
                mSimpleLineChart.setYItem(yChart);
                HashMap<Integer, Integer> pointMap = new HashMap();
                for (int i = 0; i < xChart.length; i++) {
                    pointMap.put(i, currentValue[i]);
                }
                mSimpleLineChart.setData(pointMap);
                break;
        }


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







    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    @Override
    protected void onStop() {
        Log.d("CustomDialogActivity:", "onStop");

        switch (functionSelect) {
            case "Stat":
                Toast.makeText(getContext(), "Stat dialog finish", Toast.LENGTH_SHORT).show();
                break;

            case "Alarm":
                mDialogResult.finish("FUCK");
                mDialogResult.isAlarmOn1(isAlarmOn1);
                mDialogResult.alarmSetTime1(txAlarmSetTime1.getText().toString());
                mDialogResult.alarmSetSchedule1("");
                mDialogResult.alarmIntent1("");
                mDialogResult.selectedItems(selectedItems);
                mDialogResult.checkedItems(checkedItems);


                alarmCal = Calendar.getInstance();
                alarmCal.add(Calendar.DATE, 1);
                //todo 呼叫broadcast執行鬧鐘

                Toast.makeText(getContext(), "Alarm dialog finish", Toast.LENGTH_SHORT).show();

                break;

            case "Chart":
                Toast.makeText(getContext(), "Chart dialog finish", Toast.LENGTH_SHORT).show();

                break;
        }
        super.onStop();


    }
    void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
        void isAlarmOn1(Boolean b);
        void alarmSetTime1(String hhmm);
        void alarmSetSchedule1(String schedule);
        void alarmIntent1(String function);
        void selectedItems(ArrayList selectedItems);
        void checkedItems(boolean[] checkedItems);
    }

    @Override
    public void onClick(View v) {

    }
}

