package com.prolificinteractive.materialcalendarview;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * The callback used to indicate a range has been selected
 */
public interface OnRangeSelectedListener {

    /**
     * Called when a user selects a range of days.
     * There is no logic to prevent multiple calls for the same date and state.
     *
     * @param widget           the view associated with this listener
     * @param startCalendarDay 开始时间
     * @param endCalendarDay   结束时间
     * @param day              天数
     */
    void onRangeSelected(@NonNull MaterialCalendarView widget, CalendarDay startCalendarDay, CalendarDay endCalendarDay, int day);
}
