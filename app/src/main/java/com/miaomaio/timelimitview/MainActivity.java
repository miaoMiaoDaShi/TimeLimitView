package com.miaomaio.timelimitview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class MainActivity extends AppCompatActivity {
    private Button mBtnSelectTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnSelectTime = findViewById(R.id.mBtnSelectTime);


        mBtnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeLimitDialog.newInstance().show(getSupportFragmentManager(), "");
            }
        });


    }
}
