package com.miaomaio.timelimitview;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/8/14
 * Description :
 */
public class TimeLimitDialog extends DialogFragment {

    private static final String TAG = "TimeLimitDialog";

    public static TimeLimitDialog newInstance(int selectMode,long startTime,long endTime) {

        Bundle args = new Bundle();
        args.putInt("selectMode", selectMode);
        args.putLong("startTime",startTime);
        args.putLong("endTime",endTime);
        TimeLimitDialog fragment = new TimeLimitDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_time_limit, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        final WindowManager.LayoutParams layoutParams = Objects.requireNonNull(getDialog().getWindow()).getAttributes();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        getDialog().getWindow().setAttributes(layoutParams);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialCalendarView materialCalendarView = view.findViewById(R.id.mMaterialCalendarView);

        materialCalendarView.setWeekDayLabels(new String[]{"日", "一", "二", "三", "四", "五", "六"});
        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return new SimpleDateFormat("yyyy年MM月").format(day.getTimeInMillis());
            }
        });
        try {
            final int selectMode = getArguments().getInt("selectMode", MaterialCalendarView.SELECTION_MODE_SINGLE);
            final long startTime = getArguments().getLong("startTime",0);
            final long endTime = getArguments().getLong("endTime",0);


            CalendarDay startCalendarDay = CalendarDay.from(startTime);
            CalendarDay endCalendarDay = CalendarDay.from(endTime);

            materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
            materialCalendarView.setSelectionMode(selectMode);


            materialCalendarView.post(new Runnable() {
                @Override
                public void run() {
                    if (startTime!=0&&endTime!=0&&endTime>=startTime) {
                        materialCalendarView.onDateClicked(startCalendarDay, true);
                        materialCalendarView.onDateClicked(endCalendarDay, true);
                        materialCalendarView.setCurrentDate(endTime);
                    }

                    if (selectMode== MaterialCalendarView.SELECTION_MODE_RANGE) {
                        materialCalendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
                            @Override
                            public void onRangeSelected(@NonNull MaterialCalendarView widget, CalendarDay startCalendarDay, CalendarDay endCalendarDay, int day) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mOnRangeSelectedListener != null) {
                                            mOnRangeSelectedListener.onRangeSelected(widget, startCalendarDay, endCalendarDay, day);
                                        }
                                        dismiss();
                                    }
                                }, 1000);
                            }


                        });
                    } else if(selectMode== MaterialCalendarView.SELECTION_MODE_SINGLE){
                        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                            @Override
                            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mOnDateSelectedListener != null) {
                                            mOnDateSelectedListener.onDateSelected(widget, date, selected);
                                        }
                                        dismiss();
                                    }
                                }, 1000);

                            }
                        });
                    }



                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }

    private OnRangeSelectedListener mOnRangeSelectedListener;

    public void setOnRangeSelectedListener(OnRangeSelectedListener onRangeSelectedListener) {
        mOnRangeSelectedListener = onRangeSelectedListener;
    }

    private OnDateSelectedListener mOnDateSelectedListener;


    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }
}
