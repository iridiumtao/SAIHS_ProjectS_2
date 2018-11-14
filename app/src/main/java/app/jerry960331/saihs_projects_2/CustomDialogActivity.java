package app.jerry960331.saihs_projects_2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class CustomDialogActivity extends Dialog implements View.OnClickListener {
    Activity c;
    String functionSelect, currentStat;
    int socketSelect;
    int currentNow, currentAve;
    boolean isSWOn;
    private TextView txCurrentStat, txCurrentNow, txCurrentAve, txCurrentDescription;
    private ImageView imageCurrentStat;
    private SimpleLineChart mSimpleLineChart;

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

                if (currentNow == 0) {//todo ERROR
                    txCurrentDescription.setText(R.string.current_description_off);
                    imageCurrentStat.setImageResource(R.drawable.dot_black_48dp);
                }else if (currentNow > 0 && currentNow < 8000){
                    txCurrentDescription.setText(R.string.current_description_good);
                    imageCurrentStat.setImageResource(R.drawable.dot_green_48dp);
                }else if (currentNow > 8000 && currentNow < 15000) {
                    imageCurrentStat.setImageResource(R.drawable.dot_orange_48dp);
                }else if (currentNow > 15000) {
                    imageCurrentStat.setImageResource(R.drawable.dot_red_48dp);
                }else {
                    txCurrentDescription.setText(R.string.current_description_off);
                    imageCurrentStat.setImageResource(R.drawable.dot_black_48dp);
                }
                break;
            case "Alarm":
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


    @Override
    public void onClick(View v) {

    }


}
