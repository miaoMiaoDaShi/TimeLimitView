package com.prolificinteractive.materialcalendarview;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.PopupWindow;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SHOW_DEFAULTS;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOtherMonths;
import static java.util.Calendar.DATE;

abstract class CalendarPagerView extends ViewGroup implements View.OnClickListener, View.OnLongClickListener {

    protected static final int DEFAULT_DAYS_IN_WEEK = 7;
    protected static final int DEFAULT_MAX_WEEKS = 5;
    protected static final int DAY_NAMES_ROW = 1;

    private static final Calendar tempWorkingCalendar = CalendarUtils.getInstance();

    private final ArrayList<WeekDayView> weekDayViews = new ArrayList<>();
    private final ArrayList<DecoratorResult> decoratorResults = new ArrayList<>();
    @ShowOtherDates
    protected int showOtherDates = SHOW_DEFAULTS;
    private MaterialCalendarView mcv;
    private CalendarDay firstViewDay;
    private CalendarDay minDate = null;
    private CalendarDay maxDate = null;
    private int firstDayOfWeek;
    protected boolean showWeekDays;


    private final Collection<DayView> dayViews = new ArrayList<>();


    public Collection<DayView> getDayViews() {
        return dayViews;
    }

    public CalendarPagerView(@NonNull MaterialCalendarView view,
                             CalendarDay firstViewDay,
                             int firstDayOfWeek,
                             boolean showWeekDays) {
        super(view.getContext());
        this.mcv = view;
        this.firstViewDay = firstViewDay;
        this.firstDayOfWeek = firstDayOfWeek;
        this.showWeekDays = showWeekDays;

        setClipChildren(false);
        setClipToPadding(false);

        if (LocalUtils.isRTL()) {
            this.setRotationY(180);
        }

        if (showWeekDays) {
            buildWeekDays(resetAndGetWorkingCalendar());
        }
        buildDayViews(dayViews, resetAndGetWorkingCalendar());
    }



    private void buildWeekDays(Calendar calendar) {
        for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
            WeekDayView weekDayView = new WeekDayView(getContext(), CalendarUtils.getDayOfWeek(calendar));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                weekDayView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
            weekDayViews.add(weekDayView);
            addView(weekDayView);
            calendar.add(DATE, 1);
        }
    }

    protected void addDayView(Collection<DayView> dayViews, Calendar calendar) {
        CalendarDay day = CalendarDay.from(calendar);
        DayView dayView = new DayView(getContext(), day);
        dayView.setSelectionMode(mcv.getSelectionMode());
        dayView.setOnClickListener(this);
        dayView.setOnLongClickListener(this);
        dayViews.add(dayView);
        addView(dayView, new LayoutParams());

        calendar.add(DATE, 1);
    }

    protected Calendar resetAndGetWorkingCalendar() {
        getFirstViewDay().copyTo(tempWorkingCalendar);
        //noinspection ResourceType
        tempWorkingCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
        int dow = CalendarUtils.getDayOfWeek(tempWorkingCalendar);
        int delta = getFirstDayOfWeek() - dow;
        //If the delta is positive, we want to remove a week
        boolean removeRow = showOtherMonths(showOtherDates) ? delta >= 0 : delta > 0;
        if (removeRow) {
            delta -= DEFAULT_DAYS_IN_WEEK;
        }
        tempWorkingCalendar.add(DATE, delta);
        return tempWorkingCalendar;
    }

    protected int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    protected abstract void buildDayViews(Collection<DayView> dayViews, Calendar calendar);

    protected abstract boolean isDayEnabled(CalendarDay day);

    void setDayViewDecorators(List<DecoratorResult> results) {
        this.decoratorResults.clear();
        if (results != null) {
            this.decoratorResults.addAll(results);
        }
        invalidateDecorators();
    }

    public void setWeekDayTextAppearance(int taId) {
        for (WeekDayView weekDayView : weekDayViews) {
            weekDayView.setTextAppearance(getContext(), taId);
        }
    }


    public void setShowOtherDates(@ShowOtherDates int showFlags) {
        this.showOtherDates = showFlags;
        updateUi();
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        for (DayView dayView : dayViews) {
            dayView.setOnClickListener(selectionEnabled ? this : null);
            dayView.setClickable(selectionEnabled);
        }
    }


    public void setWeekDayFormatter(WeekDayFormatter formatter) {
        for (WeekDayView dayView : weekDayViews) {
            dayView.setWeekDayFormatter(formatter);
        }
    }


    public void setMinimumDate(CalendarDay minDate) {
        this.minDate = minDate;
        updateUi();
    }

    public void setMaximumDate(CalendarDay maxDate) {
        this.maxDate = maxDate;
        updateUi();
    }

    private static final String TAG = "CalendarPagerView";

    public void setSelectedDates(ArrayList<CalendarDay> dates) {
        final int[] location = new int[2];
        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();

            if (dates.contains(day)) {
                dayView.setDay(dates.get(dates.indexOf(day)));
            }
            dayView.setChecked(dates != null && dates.contains(day));


        }
        postInvalidate();
    }

    protected void updateUi() {
        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();
            dayView.setupSelection(
                    showOtherDates, day.isInRange(minDate, maxDate), isDayEnabled(day));
        }
        postInvalidate();
    }

    protected void invalidateDecorators() {
        final DayViewFacade facadeAccumulator = new DayViewFacade();
        for (DayView dayView : dayViews) {
            facadeAccumulator.reset();
            for (DecoratorResult result : decoratorResults) {
                if (result.decorator.shouldDecorate(dayView.getDate())) {
                    result.result.applyTo(facadeAccumulator);
                }
            }
            // dayView.applyFacade(facadeAccumulator);
        }
    }

    @Override
    public void onClick(final View v) {
        if (v instanceof DayView) {
            final DayView dayView = (DayView) v;
            mcv.onDateClicked(dayView);
        }
    }

    @Override
    public boolean onLongClick(final View v) {
        if (v instanceof DayView) {
            final DayView dayView = (DayView) v;
            mcv.onDateLongClicked(dayView);
            return true;
        }
        return false;
    }

    /*
     * Custom ViewGroup Code
     */

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //We expect to be somewhere inside a MaterialCalendarView, which should measure EXACTLY
        if (specHeightMode == MeasureSpec.UNSPECIFIED || specWidthMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("CalendarPagerView should never be left to decide it's size");
        }

        //The spec width should be a correct multiple
        final int measureTileWidth = specWidthSize / DEFAULT_DAYS_IN_WEEK;
        final int measureTileHeight = specHeightSize / getRows();

        //Just use the spec sizes
        setMeasuredDimension(specWidthSize, specHeightSize);

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileWidth,
                    MeasureSpec.EXACTLY
            );

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileHeight,
                    MeasureSpec.EXACTLY
            );

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    /**
     * Return the number of rows to display per page
     *
     * @return
     */
    protected abstract int getRows();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int parentWidth = getWidth();
        final int count = getChildCount();
        final int parentLeft = 0;
        final int parentRight = parentWidth;

        int childTop = 0;
        int childLeft = parentLeft;
        int childRight = parentRight;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (LocalUtils.isRTL()) {
                child.layout(childRight - width, childTop, childRight, childTop + height);
                childRight -= width;
            } else {
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
                childLeft += width;
            }

            //We should warp every so many children
            if (i % DEFAULT_DAYS_IN_WEEK == (DEFAULT_DAYS_IN_WEEK - 1)) {
                childLeft = parentLeft;
                childRight = parentRight;
                childTop += height;
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams();
    }


    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CalendarPagerView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CalendarPagerView.class.getName());
    }

    protected CalendarDay getFirstViewDay() {
        return firstViewDay;
    }

    /**
     * Simple layout params class for MonthView, since every child is the same size
     */
    protected static class LayoutParams extends MarginLayoutParams {

        /**
         * {@inheritDoc}
         */
        public LayoutParams() {
            super(WRAP_CONTENT, WRAP_CONTENT);
        }
    }
}