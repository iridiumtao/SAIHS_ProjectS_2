package app.jerry960331.saihs_projects_2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.os.Looper.getMainLooper;

public class CustomDialogActivity extends Dialog implements View.OnClickListener {
    Activity c;
    String functionSelect, currentStat;
    int socketSelect;

    //狀態
    int currentNow;
    int powerNow;
    Double currentAve;
    boolean isSWOn;
    TextView txCurrentStat, txCurrentNow, txPowerNow, txCurrentAve, txCurrentDescription;
    ImageView imageCurrentStat;
    boolean devModeValue = false;

    //鬧鐘
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
    private Calendar alarmCal;
    private OnAlarmDialogResult alarmDialogResult; //回傳鬧鐘資料
    private OnChartDialogResult chartDialogResult;
    LinearLayout alarmSet1;
    ArrayList selectedItems = new ArrayList();
    boolean[] checkedItems;
    private Handler clockHandler;
    Calendar cal = Calendar.getInstance();
    int safeCurrentValue;
    String current;

    //倒數計時器
    Button btnGotoAlarm , btnTimerSocket1, btnTimerSocket2, btnTimerSocket3, btnTimerSocket4;
    EditText editTextHour, editTextMinute, editTextSecond;
    ImageButton btnStartStop, btnReset;
    Switch swTimer;
    private int timeSet;
    private long timeCountInMilliSeconds = 1 * 60000;
    private ProgressBar progressBarCircle;
    private boolean timerS1 = false, timerS2 = false, timerS3 = false, timerS4 = false;

    Long remainTime = (long) 0;


    String[] xChart = {};
    String[] yChart = {};
    int[] currentValue = {};

    LineChart chart;
    ArrayList<Entry> yValues = new ArrayList<>();
    private Handler getCurrentHandler;


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
                AlarmDialog();
                break;

            case "Chart":
                Log.d("d", "chart");
                setContentView(R.layout.chart_dialog);
                SimpleLineChart mSimpleLineChart = (SimpleLineChart) findViewById(R.id.simpleLineChart);
                LinearLayout simpleLineChartLinerLayout = findViewById(R.id.simpleLineChartLinerLayout);

                chart = findViewById(R.id.currentChartRT);

                simpleLineChartLinerLayout.setVisibility(View.VISIBLE);
                chart.setVisibility(View.GONE);


                mSimpleLineChart.setXItem(xChart);
                mSimpleLineChart.setYItem(yChart);
                HashMap<Integer, Integer> pointMap = new HashMap();
                for (int i = 0; i < xChart.length; i++) {
                    pointMap.put(i, currentValue[i]);
                    Log.d("xItem", currentValue[i] + "");
                }
                mSimpleLineChart.setData(pointMap);

                break;

            case "Chart2":
                Log.d("d", "chart");
                setContentView(R.layout.chart_dialog);
                simpleLineChartLinerLayout = findViewById(R.id.simpleLineChartLinerLayout);
                chart = findViewById(R.id.currentChartRT);

                chart.setVisibility(View.VISIBLE);
                simpleLineChartLinerLayout.setVisibility(View.GONE);

                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setDrawGridBackground(false);
                chart.setPinchZoom(true);

                LineData data = new LineData();

                chart.setData(data);

                //圖例
                Legend l = chart.getLegend();
                l.setForm(Legend.LegendForm.LINE);
                XAxis xl = chart.getXAxis();
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);
                //todo xl.setValueFormatter();

                LimitLine upperLimit = new LimitLine(safeCurrentValue, c.getString(R.string.safe_current_value));
                upperLimit.setLineWidth(4f);
                upperLimit.enableDashedLine(10f, 10f, 10f);
                upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                upperLimit.setTextSize(15f);

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setAxisMinimum(0f);
                leftAxis.setDrawLimitLinesBehindData(true);
                leftAxis.addLimitLine(upperLimit);
                leftAxis.setDrawGridLines(true);

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                if (current == null || current == ""){
                    current = "0";
                }

                if (Integer.parseInt(current) > safeCurrentValue){ //停止繪製表格
                    break;
                }

                getCurrentHandler = new Handler(getMainLooper());
                getCurrentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        LineData data = chart.getData();
                        if (data != null) {
                            ILineDataSet set = data.getDataSetByIndex(0);
                            // set.addEntry(...); // can be called as well
                            if (set == null) {
                                set = createSet();
                                data.addDataSet(set);
                            }

                            if (devModeValue){
                                data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 100)), 0);
                            }else {
                                data.addEntry(new Entry(set.getEntryCount(), Integer.parseInt(current)), 0);
                            }


                            if (current == null) {
                                current = "0";
                            }
                            data.notifyDataChanged();
                            chart.notifyDataSetChanged();
                            chart.setVisibleXRangeMaximum(30);
                            chart.setVisibleXRangeMinimum(10);
                            chart.moveViewToX(data.getEntryCount());
                            Log.d("i", set.getEntryCount() + "");
                            chart.invalidate();
                            getCurrentHandler.postDelayed(this, 1000);
                        }
                    }
                }, 10);
                break;
        }


    }

    private void AlarmDialog() {
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

        clockHandler = new Handler(getMainLooper());
        clockHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txNowTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                txNowDate.setText(new SimpleDateFormat("MM月dd日 E", Locale.getDefault()).format(new Date()));
                clockHandler.postDelayed(this, 1000);
            }
        }, 10);

        final String[] date = {"週一", "週二", "週三", "週四", "週五", "週六", "週日"};
        //若鬧鐘先前已開啟，則將圖示開啟
        if (isAlarmOn1) {
            btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_on);
            isAlarmOn1 = true;
            Toast.makeText(getContext(), "鬧鐘先前設定開啟", Toast.LENGTH_SHORT).show();
        } else {
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
        } catch (Exception ignore) {
        }

        //若未設定週期 則不允許鬧鐘開啟
        if (txAlarmSetSchedule1.getText().toString().equals(getContext().getString(R.string.not_set))) {
            btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
            isAlarmOn1 = false;
        }

        //如果未設定鬧鐘時間 則預設為目前時間
        if (alarmSetTime1 == "") {
            txAlarmSetTime1.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        } else {
            txAlarmSetTime1.setText(alarmSetTime1);
        }


        alarmSet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = Integer.parseInt(txAlarmSetTime1.getText().toString().substring(0, 2));
                int minute = Integer.parseInt(txAlarmSetTime1.getText().toString().substring(3));

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txAlarmSetTime1.setText(String.format("%02d:%02d", hourOfDay, minute, Locale.getDefault()));
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE,minute);
                        cal.set(Calendar.SECOND,0);

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
                        txAlarmSetSchedule1.setText("不重複");
                    }
                });
                datePickDialog.setMultiChoiceItems(date, checkedItems, new OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(which);
                            Toast.makeText(getContext(), which + "", Toast.LENGTH_SHORT).show();

                        } else if (selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(which));
                            Toast.makeText(getContext(), "-" + which, Toast.LENGTH_SHORT).show();
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
                        } catch (Exception e) {
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
                        } catch (Exception e) {
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
                if (txAlarmSetSchedule1.getText().toString().equals(getContext().getString(R.string.not_set))) {
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                    isAlarmOn1 = false;
                    Toast.makeText(getContext(), "未設定週期", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isAlarmOn1) {//turn on
                    //todo 記住這個鬧鐘
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_on);

                    isAlarmOn1 = true;
                } else {//turn off
                    //todo 取消這個鬧鐘
                    btnAlarmIsOn1.setImageResource(R.drawable.icon_alarm_off);
                    isAlarmOn1 = false;
                }

            }
        });

        //todo =============================================Timer
        btnGotoTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.alarm_timer);
                swTimer = findViewById(R.id.swTimer);
                btnReset = findViewById(R.id.btnReset);
                btnGotoAlarm = findViewById(R.id.btnGotoAlarm);
                btnStartStop = findViewById(R.id.btnStartStop);
                editTextHour = findViewById(R.id.editTextHour);
                editTextMinute = findViewById(R.id.editTextMinute);
                editTextSecond = findViewById(R.id.editTextSecond);
                btnTimerSocket1 = findViewById(R.id.btnTimerSocket1);
                btnTimerSocket2 = findViewById(R.id.btnTimerSocket2);
                btnTimerSocket3 = findViewById(R.id.btnTimerSocket3);
                btnTimerSocket4 = findViewById(R.id.btnTimerSocket4);
                progressBarCircle = findViewById(R.id.progressBarCircle);

                btnGotoAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clockHandler.removeCallbacksAndMessages(null);
                        AlarmDialog();
                    }
                });

                btnTimerSocket1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timerS1){
                            btnTimerSocket1.setBackground(getContext().getDrawable(R.drawable.button_auto));
                            btnTimerSocket1.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                            timerS1 = false;
                        }else {
                            btnTimerSocket1.setBackground(getContext().getDrawable(R.drawable.button_auto_on));
                            btnTimerSocket1.setTextColor(getContext().getResources().getColor(R.color.white));
                            timerS1 = true;
                        }
                    }
                });
                btnTimerSocket2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timerS2){
                            btnTimerSocket2.setBackground(getContext().getDrawable(R.drawable.button_auto));
                            btnTimerSocket2.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                            timerS2 = false;
                        }else {
                            btnTimerSocket2.setBackground(getContext().getDrawable(R.drawable.button_auto_on));
                            btnTimerSocket2.setTextColor(getContext().getResources().getColor(R.color.white));
                            timerS2 = true;
                        }
                    }
                });
                btnTimerSocket3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timerS3){
                            btnTimerSocket3.setBackground(getContext().getDrawable(R.drawable.button_auto));
                            btnTimerSocket3.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                            timerS3 = false;
                        }else {
                            btnTimerSocket3.setBackground(getContext().getDrawable(R.drawable.button_auto_on));
                            btnTimerSocket3.setTextColor(getContext().getResources().getColor(R.color.white));
                            timerS3 = true;
                        }
                    }
                });
                btnTimerSocket4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timerS4){
                            btnTimerSocket4.setBackground(getContext().getDrawable(R.drawable.button_auto));
                            btnTimerSocket4.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                            timerS4 = false;
                        }else {
                            btnTimerSocket4.setBackground(getContext().getDrawable(R.drawable.button_auto_on));
                            btnTimerSocket4.setTextColor(getContext().getResources().getColor(R.color.white));
                            timerS4 = true;
                        }
                    }
                });

                btnStartStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                    }
                });

                btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });








            }
        });
    }



    private LineDataSet createSet() {
        Log.d("call", "createSet()");
        LineDataSet set;
        if (devModeValue){
            set = new LineDataSet(null, "電流值(開發模式)");

        }else {
            set = new LineDataSet(null, "電流值");
        }
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.rgb(0, 185, 169)); //Color.rgb(0, 185, 169) == colorPrimary
        set.setCircleColor(Color.rgb(0, 185, 169));
        set.setCircleHoleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.rgb(0, 185, 169));
        set.setHighLightColor(Color.rgb(244, 117, 117));
        //set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(true);
        return set;
    }




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
                alarmDialogResult.finish("Alarm Finish");
                alarmDialogResult.isAlarmOn1(isAlarmOn1);
                alarmDialogResult.alarmSetTime1(txAlarmSetTime1.getText().toString());
                alarmDialogResult.alarmSetSchedule1("");//沒有
                alarmDialogResult.alarmIntent1("");//todo
                alarmDialogResult.selectedItems(selectedItems);
                alarmDialogResult.checkedItems(checkedItems);
                if(isAlarmOn1){
                    alarmDialogResult.callStartAlarm(cal);
                }else {
                   alarmDialogResult.callStartAlarm(null);
                }
                Log.d("selectedItems", selectedItems + "");

                alarmCal = Calendar.getInstance();
                alarmCal.add(Calendar.DATE, 1);
                //todo 呼叫broadcast執行鬧鐘

                clockHandler.removeCallbacksAndMessages(null);


                break;

            case "Chart":
                //廢止

                Toast.makeText(getContext(), "Chart dialog finish", Toast.LENGTH_SHORT).show();

                break;
            case "Chart2":
                chartDialogResult.finish("Chart2 Finish");
                getCurrentHandler.removeCallbacksAndMessages(null);

                Toast.makeText(getContext(), "Chart dialog finish", Toast.LENGTH_SHORT).show();

                break;
        }
        super.onStop();


    }

    void setAlarmDialogResult(OnAlarmDialogResult dialogResult) {
        alarmDialogResult = dialogResult;
    }

    public interface OnAlarmDialogResult {
        void finish(String result);

        void isAlarmOn1(Boolean b);

        void alarmSetTime1(String hhmm);

        void alarmSetSchedule1(String schedule);

        void alarmIntent1(String function);

        void selectedItems(ArrayList selectedItems);

        void checkedItems(boolean[] checkedItems);

        void callStartAlarm(Calendar cal);
    }


    void setChartDialogResult(OnChartDialogResult dialogResult) {
        chartDialogResult = dialogResult;
    }

    public interface OnChartDialogResult {
        void finish(String result);
    }

    @Override
    public void onClick(View v) {

    }
}

