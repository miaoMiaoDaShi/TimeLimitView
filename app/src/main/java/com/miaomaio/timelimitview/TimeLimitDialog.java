package com.miaomaio.timelimitview;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.util.Objects;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/8/14
 * Description :
 */
public class TimeLimitDialog extends DialogFragment {
    private MaterialCalendarView mMaterialCalendarView;

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
        mMaterialCalendarView = view.findViewById(R.id.mMaterialCalendarView);

        mMaterialCalendarView.setWeekDayLabels(new String[]{"日", "一", "二", "三", "四", "五", "六"});
        mMaterialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return String.format("%s年%s月", day.getYear(), day.getMonth() + 1);
            }
        });
        mMaterialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        mMaterialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);


    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }

}
