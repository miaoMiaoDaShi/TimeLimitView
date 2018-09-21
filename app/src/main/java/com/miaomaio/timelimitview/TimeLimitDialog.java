package com.miaomaio.timelimitview;

import android.app.Dialog;
import android.os.Bundle;
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

    public static TimeLimitDialog newInstance() {

        Bundle args = new Bundle();

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
                return String.format("%s年%s月", day.getYear(), day.getMonth() + 1);
            }
        });
        try {
            final long startTime = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-5").getTime();
            final long endTime = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-28").getTime();


            CalendarDay startCalendarDay = CalendarDay.from(startTime);
            CalendarDay endCalendarDay = CalendarDay.from(endTime);

            materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
            materialCalendarView.setDay(11);
            materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);


            materialCalendarView.post(new Runnable() {
                @Override
                public void run() {
                    materialCalendarView.onDateClicked(startCalendarDay, true);
                    materialCalendarView.onDateClicked(endCalendarDay, true);

                    materialCalendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
                        @Override
                        public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                            CalendarDay calendarDay1 = dates.get(0);
                            CalendarDay calendarDay2 = dates.get(dates.size() - 1);
                            int day = (int) ((calendarDay2.getCalendar().getTimeInMillis()-calendarDay1.getCalendar().getTimeInMillis())/1000/24/3600);
                            Toast.makeText(getContext(), String.format("开始%s 结束%s,%d天",calendarDay1,calendarDay2,day+1), Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    });
                }
            });


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }

}
