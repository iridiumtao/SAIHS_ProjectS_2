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
    String functionSelect;
    int socketSelect;
    int currentNow, currentAve;
    boolean isSWOn;
    private TextView txCurrentStat, txCurrentNow, txCurrentAve, txCurrentDescription;
    private ImageView imageCurrentStat;

    String[] xChart = {};
    String[] yChart = {};


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
                setContentView(R.layout.current_dialog);
                txCurrentStat = findViewById(R.id.txCurrentStat);
                txCurrentNow = findViewById(R.id.txCurrentNow);
                txCurrentAve = findViewById(R.id.txCurrentAve);
                txCurrentDescription = findViewById(R.id.txCurrentDescription);
                imageCurrentStat = findViewById(R.id.imageCurrentStat);

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

                break;
        }
    }


    @Override
    public void onClick(View v) {

    }

    public class SimpleLineChart extends View {
        //View 的宽和高
        private int mWidth, mHeight;

        //Y轴字体的大小
        private float mYAxisFontSize = 24;

        //线的颜色
        private int mLineColor = Color.parseColor("#00BCD4");

        //线条的宽度
        private float mStrokeWidth = 8.0f;

        //点的集合
        private HashMap<Integer, Integer> mPointMap;

        //点的半径
        private float mPointRadius = 10;

        //没有数据的时候的内容
        private String mNoDataMsg = "no data";

        //X轴的文字
        String[] mXAxis = {};

        //Y轴的文字
        String[] mYAxis = {};

        public SimpleLineChart(Context context) {
            this(context, null);
        }

        public SimpleLineChart(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SimpleLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthMode == MeasureSpec.EXACTLY) {
                mWidth = widthSize;
            }else if(widthMode == MeasureSpec.AT_MOST){
                throw new IllegalArgumentException("width must be EXACTLY,you should set like android:width=\"200dp\"");
            }

            if (heightMode == MeasureSpec.EXACTLY) {
                mHeight = heightSize;
            }else if(widthMeasureSpec == MeasureSpec.AT_MOST){

                throw new IllegalArgumentException("height must be EXACTLY,you should set like android:height=\"200dp\"");
            }

            setMeasuredDimension(mWidth, mHeight);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            if(mXAxis.length==0||mYAxis.length==0){
                throw new IllegalArgumentException("X or Y items is null");
            }
            //画坐标线的轴
            Paint axisPaint = new Paint();
            axisPaint.setTextSize(mYAxisFontSize);
            axisPaint.setColor(Color.parseColor("#3F51B5"));

            if (mPointMap == null || mPointMap.size() == 0) {
                int textLength = (int) axisPaint.measureText(mNoDataMsg);
                canvas.drawText(mNoDataMsg, mWidth/2 - textLength/2, mHeight/2, axisPaint);
            } else {
                //画 Y 轴


                //存放每个Y轴的坐标
                int[] yPoints = new int[mYAxis.length];


                //计算Y轴 每个刻度的间距
                int yInterval = (int) ((mHeight - mYAxisFontSize - 2) / (mYAxis.length));

                //测量Y轴文字的高度 用来画第一个数
                Paint.FontMetrics fm = axisPaint.getFontMetrics();
                int yItemHeight = (int) Math.ceil(fm.descent - fm.ascent);

                Log.e("wing", mHeight + "");
                for (int i = 0; i < mYAxis.length; i++) {
                    canvas.drawText(mYAxis[i], 0, mYAxisFontSize + i * yInterval, axisPaint);
                    yPoints[i] = (int) (mYAxisFontSize + i * yInterval);


                }


                //画 X 轴

                //x轴的刻度集合
                int[] xPoints = new int[mXAxis.length];

                Log.e("wing", xPoints.length + "");
                //计算Y轴开始的原点坐标
                int xItemX = (int) axisPaint.measureText(mYAxis[1]);

                //X轴偏移量
                int xOffset = 50;
                //计算x轴 刻度间距
                int xInterval = (int) ((mWidth - xOffset) / (mXAxis.length));
                //获取X轴刻度Y坐标
                int xItemY = (int) (mYAxisFontSize + mYAxis.length * yInterval);

                for (int i = 0; i < mXAxis.length; i++) {
                    canvas.drawText(mXAxis[i], i * xInterval + xItemX + xOffset, xItemY, axisPaint);
                    xPoints[i] = (int) (i * xInterval + xItemX + axisPaint.measureText(mXAxis[i]) / 2 + xOffset + 10);
//            Log.e("wing", xPoints[i] + "");
                }


                //画点
                Paint pointPaint = new Paint();

                pointPaint.setColor(mLineColor);

                Paint linePaint = new Paint();

                linePaint.setColor(mLineColor);
                linePaint.setAntiAlias(true);
                //设置线条宽度
                linePaint.setStrokeWidth(mStrokeWidth);
                pointPaint.setStyle(Paint.Style.FILL);


                for (int i = 0; i < mXAxis.length; i++) {
                    if (mPointMap.get(i) == null) {
                        throw new IllegalArgumentException("PointMap has incomplete data!");
                    }

                    //画点
                    canvas.drawCircle(xPoints[i], yPoints[mPointMap.get(i)], mPointRadius, pointPaint);
                    if (i > 0) {
                        canvas.drawLine(xPoints[i - 1], yPoints[mPointMap.get(i - 1)], xPoints[i], yPoints[mPointMap.get(i)], linePaint);
                    }
                }

            }
        }

        /**
         * 设置map数据
         * @param data
         */
        public void setData(HashMap<Integer,Integer> data){
            mPointMap = data;
            invalidate();
        }

        /**
         * 设置Y轴文字
         * @param yItem
         */
        public void setYItem(String[] yItem){
            mYAxis = yItem;
        }

        /**
         * 设置X轴文字
         * @param xItem
         */
        public void setXItem(String[] xItem){
            mXAxis = xItem;
        }

        public void setLineColor(int color){
            mLineColor = color;
            invalidate();
        }
    }
}
