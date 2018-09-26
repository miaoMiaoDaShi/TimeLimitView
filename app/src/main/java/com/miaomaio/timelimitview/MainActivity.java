package com.miaomaio.timelimitview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private Button mBtnSelectSingleTime;
    private Button mBtnSelectRangeTime;
    private TextView mTvSingleTime;
    private TextView mTvRangeTime;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private CalendarDay mPreStratCalendarDay;
    private CalendarDay mPreEndCalendarDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnSelectSingleTime = findViewById(R.id.mBtnSelectSingleTime);
        mBtnSelectRangeTime = findViewById(R.id.mBtnSelectRangeTime);
        mTvSingleTime = findViewById(R.id.mTvSingleTime);
        mTvRangeTime = findViewById(R.id.mTvRangeTime);


       //选择单天
        mBtnSelectSingleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeLimitDialog dialog = TimeLimitDialog.newInstance(MaterialCalendarView.SELECTION_MODE_SINGLE, 0, 0);
                dialog.setOnDateSelectedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        mTvSingleTime.setText(mSimpleDateFormat.format(date.getTimeInMillis()));
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
            }
        });


        //范围选择
        mBtnSelectRangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long stratTime = mPreStratCalendarDay == null ? 0 : mPreStratCalendarDay.getTimeInMillis();
                long endTime = mPreEndCalendarDay == null ? 0 : mPreEndCalendarDay.getTimeInMillis();

                TimeLimitDialog dialog = TimeLimitDialog.newInstance(MaterialCalendarView.SELECTION_MODE_RANGE, stratTime, endTime);
                dialog.setOnRangeSelectedListener(new OnRangeSelectedListener() {
                    @Override
                    public void onRangeSelected(@NonNull MaterialCalendarView widget, CalendarDay startCalendarDay, CalendarDay endCalendarDay, int day) {
                        mPreStratCalendarDay = startCalendarDay;
                        mPreEndCalendarDay = endCalendarDay;
                        mTvRangeTime.setText(String.format("%s至%s 共%d天", mSimpleDateFormat.format(startCalendarDay.getTimeInMillis()), mSimpleDateFormat.format(endCalendarDay.getTimeInMillis()), day));
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
            }
        });


    }
}
