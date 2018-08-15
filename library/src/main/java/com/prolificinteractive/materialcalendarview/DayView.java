package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import java.util.Calendar;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showDecoratedDisabled;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOtherMonths;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOutOfRange;

/**
 * Display one day of a {@linkplain MaterialCalendarView}
 */
@SuppressLint("ViewConstructor")
class DayView extends View {

    private CalendarDay date;
    private int selectionColor = Color.BLACK;

    /**
     * 下面的文字的高度
     */
    private int mBottomTextHeight = 10;


    private final int fadeTime;
    private Drawable customBackground = null;
    private Drawable selectionDrawable;
    private Drawable mCircleDrawable;
    private DayFormatter formatter = DayFormatter.DEFAULT;
    private DayFormatter contentDescriptionFormatter = formatter;

    private boolean isInRange = true;
    private boolean isInMonth = true;
    private boolean isDecoratedDisabled = false;

    /**
     * 当前的日期
     */
    private String mCurrentDay = "";

    /**
     * 画圆点
     */
    private Paint mCirclePointPaint;

    /**
     * 画背景
     */
    private Paint mBackgroundPaint;

    /**
     * 日期
     */
    private Paint mDayTextPaint;

    /**
     * 其他月的日期颜色
     */
    private final String COLOR_OTHER_MONTH_DYA_TEXT = "#DCDCDC";
    /**
     * 当前月的日期 颜色
     */
    private final String COLOR_CURRENT_MONTH_DYA_TEXT = "#999999";
    /**
     * 开始 和结束的文字颜色
     */
    private final String COLOR_START_END_TEXT = "#ffffff";

    /**
     * 背景   颜色
     */
    private final String COLOR_BACKGROUND = "#f1f1f1";

    /**
     * 时间段开始
     */
    private boolean mIsStrat = false;
    /**
     * 时间段结束
     */
    private boolean mIsEnd = false;
    /**
     * 最左边 选中时生效
     */
    private boolean mIsLeft = false;
    /**
     * 最右边  选中时 生效
     */
    private boolean mIsRight = false;

    /**
     * 选中 ???
     */
    private boolean mIsChecked = false;

    /**
     * 已经选择了结束时间
     */
    private boolean mIsEndChecked = false;


    @ShowOtherDates
    private int showOtherDates = MaterialCalendarView.SHOW_DEFAULTS;

    @MaterialCalendarView.SelectionMode
    private int selectionMode = 0;


    public DayView(Context context, CalendarDay day) {
        super(context);

        mBottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomTextPaint.setColor(Color.BLACK);
        mBottomTextPaint.setTextSize(spToPx(11));


        mDayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mDayTextPaint.setTextSize(spToPx(14));

        mCirclePointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePointPaint.setColor(Color.BLACK);
        mCirclePointPaint.setShadowLayer(dpToPx(5), 0, dpToPx(4), Color.parseColor("#20222222"));

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(Color.parseColor(COLOR_BACKGROUND));
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        setLayerType(LAYER_TYPE_SOFTWARE, mCirclePointPaint);
        fadeTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        // setGravity(Gravity.CENTER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }

        mIsLeft = day.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
        mIsRight = day.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;


        setDay(day);
    }

    public void setDay(CalendarDay date) {
        this.date = date;
        mCurrentDay = String.valueOf(date.getDay());
    }


    public void setSelectionMode(final @MaterialCalendarView.SelectionMode int mode) {
        this.selectionMode = mode;
    }


    public CalendarDay getDate() {
        return date;
    }

    private void setEnabled() {
        boolean enabled = isInMonth && isInRange && !isDecoratedDisabled;
        super.setEnabled(isInRange && !isDecoratedDisabled);

        boolean showOtherMonths = showOtherMonths(showOtherDates);
        boolean showOutOfRange = showOutOfRange(showOtherDates) || showOtherMonths;
        boolean showDecoratedDisabled = showDecoratedDisabled(showOtherDates);

        boolean shouldBeVisible = enabled;

        if (!isInMonth && showOtherMonths) {
            shouldBeVisible = true;
        }

        if (!isInRange && showOutOfRange) {
            shouldBeVisible |= isInMonth;
        }

        if (isDecoratedDisabled && showDecoratedDisabled) {
            shouldBeVisible |= isInMonth && isInRange;
        }

        if (!isInMonth && shouldBeVisible) {
//            setTextColor(getTextColors().getColorForState(
//                    new int[]{-android.R.attr.state_enabled}, Color.GRAY));
        }
        setVisibility(shouldBeVisible ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setupSelection(@ShowOtherDates int showOtherDates, boolean inRange, boolean inMonth) {
        this.showOtherDates = showOtherDates;
        this.isInMonth = inMonth;
        this.isInRange = inRange;
        setEnabled();
    }

    private final Rect tempRect = new Rect();
    private final Rect circleDrawableRect = new Rect();

    private Paint mBottomTextPaint;

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        mIsStrat = date.isStrat();
        mIsEnd = date.isEnd();
        mIsEndChecked = date.isEndChecked();

        drawBackground(canvas);

        drawDayText(canvas);
        drawBottomText(canvas);


    }

    /**
     * 画背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {


        if (mIsChecked) {
            if (selectionMode == MaterialCalendarView.SELECTION_MODE_RANGE) {//范围选中

                //一共有以下的状态
                //1.左边 ,开始,,没有选中结束
                if (mIsLeft && mIsStrat && !mIsEndChecked) {
                    //画左半圆
                    canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
                } else if (mIsLeft && mIsStrat && mIsEndChecked) {//左边  开始 ,选择了结束
                    canvas.drawRect(mLeftBackgroundRect, mBackgroundPaint);
                    canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
                } else if (mIsRight && mIsStrat) {//右边 ,开始,,没有
                    canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);

                } else if (mIsLeft && mIsEnd) {//左边,结束
                    canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
                } else if (mIsRight && mIsEnd) {//右边结束
                    canvas.drawRect(mRightBackgroundRect, mBackgroundPaint);
                    canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
                } else if ((mIsRight || mIsLeft) && !mIsStrat && !mIsEnd) {//是左边 或者右边,非结束 开始
                    if (mIsLeft) {
                        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mBackgroundPaint);
                        canvas.drawRect(mLeftBackgroundRect, mBackgroundPaint);
                    } else if (mIsRight) {
                        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mBackgroundPaint);
                        canvas.drawRect(mRightBackgroundRect, mBackgroundPaint);
                    }
                } else if (!mIsLeft && !mIsRight) {//不是左边 右边
                    if (mIsStrat && !mIsEndChecked) {//开始 没有选中结束
                        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
                    } else if (mIsStrat && mIsEndChecked) {////开始 选中结束
                        canvas.drawRect(mLeftBackgroundRect, mBackgroundPaint);
                        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
                    } else if (mIsEnd) {
                        canvas.drawRect(mRightBackgroundRect, mBackgroundPaint);
                        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
                    } else {
                        canvas.drawRect(mBackgroundRect, mBackgroundPaint);
                    }
                }
            } else if (selectionMode == MaterialCalendarView.SELECTION_MODE_SINGLE) {//单选
                canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
            }
        }


//
//        //判断是否是开始 或结束
//        if (mIsChecked && mIsLeft) {
//
//        } else if (mIsChecked && mIsRight) {
//            //画右边的半圆
//            canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mBackgroundPaint);
//            canvas.drawRect(mRightBackgroundRect, mBackgroundPaint);
//        } else if (!mIsStrat && mIsChecked && !mIsEnd) {
//            //选中 普通状态//正方形//单选为圆
//            if (selectionMode == MaterialCalendarView.SELECTION_MODE_SINGLE) {
//                canvas.drawRect(mBackgroundRect, mBackgroundPaint);
//            }
//        }
//        if ((mIsChecked && mIsStrat)) {
//            canvas.drawCircle(mCircleX, mCircleY, mCircleX, mBackgroundPaint);
//            if (!mIsLeft && mIsEndChecked && !mIsRight) {
//
//                canvas.drawRect(mLeftBackgroundRect, mBackgroundPaint);
//            }
//            canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
//
//        } else if ((mIsChecked && mIsEnd)) {
//            canvas.drawCircle(mCircleX, mCircleY, mCircleX, mBackgroundPaint);
//            if (!mIsRight) {
//
//                canvas.drawRect(mRightBackgroundRect, mBackgroundPaint);
//            }
//            canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePointPaint);
//        }

    }

    /**
     * 画下面的文字
     *
     * @param canvas
     */
    private void drawBottomText(Canvas canvas) {
        if (!mIsChecked) {
            return;
        }
        Paint.FontMetricsInt fontMetrics = mBottomTextPaint.getFontMetricsInt();
        int baseline = (mBottomTextRect.bottom + mBottomTextRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        mBottomTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mIsStrat ? "开始" : mIsEnd ? "结束" : "", mBottomTextRect.centerX(), baseline, mBottomTextPaint);
    }

    /**
     * 画 日期  文字
     *
     * @param canvas
     */
    private void drawDayText(Canvas canvas) {
        Paint.FontMetricsInt fontMetrics = mDayTextPaint.getFontMetricsInt();
        int baseline = (mDayTextRect.bottom + mDayTextRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        mDayTextPaint.setTextAlign(Paint.Align.CENTER);
        if (mIsStrat || mIsEnd || (mIsChecked && selectionMode == MaterialCalendarView.SELECTION_MODE_SINGLE)) {//开始 或者是结束
            mDayTextPaint.setColor(Color.WHITE);
        } else if (isInMonth) {//当前月份
            mDayTextPaint.setColor(Color.parseColor(COLOR_CURRENT_MONTH_DYA_TEXT));
        } else {//其他月份
            mDayTextPaint.setColor(Color.parseColor(COLOR_OTHER_MONTH_DYA_TEXT));
        }

        canvas.drawText(mCurrentDay, mDayTextRect.centerX(), baseline, mDayTextPaint);
    }

    private void regenerateBackground() {
        if (selectionDrawable != null) {
            setBackgroundDrawable(selectionDrawable);
        } else {
            mCircleDrawable = generateBackground(selectionColor, fadeTime, circleDrawableRect);
            setBackgroundDrawable(mCircleDrawable);
        }
    }

    private static Drawable generateBackground(int color, int fadeTime, Rect bounds) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.setExitFadeDuration(fadeTime);
        drawable.addState(new int[]{android.R.attr.state_checked}, generateCircleDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.addState(new int[]{android.R.attr.state_pressed}, generateRippleDrawable(color, bounds));
        } else {
            drawable.addState(new int[]{android.R.attr.state_pressed}, generateCircleDrawable(color));
        }

        drawable.addState(new int[]{}, generateCircleDrawable(Color.TRANSPARENT));

        return drawable;
    }

    private static Drawable generateCircleDrawable(final int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable generateRippleDrawable(final int color, Rect bounds) {
        ColorStateList list = ColorStateList.valueOf(color);
        Drawable mask = generateCircleDrawable(Color.WHITE);
        RippleDrawable rippleDrawable = new RippleDrawable(list, null, mask);
//        API 21
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            rippleDrawable.setBounds(bounds);
        }

//        API 22. Technically harmless to leave on for API 21 and 23, but not worth risking for 23+
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            int center = (bounds.left + bounds.right) / 2;
            rippleDrawable.setHotspotBounds(center, bounds.top, center, bounds.bottom);
        }

        return rippleDrawable;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        calculateBounds(right - left, bottom - top);
    }

    /**
     * 计算相应的范围
     *
     * @param width
     * @param height
     */
    private void calculateBounds(int width, int height) {

        //背景
        calculateBackgroundBound(width, height);
        //计算圆点的范围
        calculateCircleBound(width, height);
        //画中间的日期 文字
        calculateDayTextBound(width, height);
        //计算下面文字范围
        calculateBottomTextBound(width, height);
    }

    private Rect mBackgroundRect;
    private Rect mLeftBackgroundRect;
    private Rect mRightBackgroundRect;

    /**
     * 背景范围
     *
     * @param width
     * @param height
     */
    private void calculateBackgroundBound(int width, int height) {
        mBackgroundRect = new Rect();
        mBackgroundRect.left = 0;
        mBackgroundRect.top = dpToPx(3);
        mBackgroundRect.bottom = height - dpToPx(mBottomTextHeight) - dpToPx(3);
        mBackgroundRect.right = width;


        mLeftBackgroundRect = new Rect();
        mLeftBackgroundRect.left = width / 2 + dpToPx(3);
        mLeftBackgroundRect.top = dpToPx(3);
        mLeftBackgroundRect.bottom = height - dpToPx(mBottomTextHeight) - dpToPx(3);
        mLeftBackgroundRect.right = width;

        mRightBackgroundRect = new Rect();
        mRightBackgroundRect.left = 0;
        mRightBackgroundRect.top = dpToPx(3);
        mRightBackgroundRect.bottom = height - dpToPx(mBottomTextHeight) - dpToPx(3);
        mRightBackgroundRect.right = width / 2;
    }


    private Rect mDayTextRect;

    private void calculateDayTextBound(int width, int height) {
        mDayTextRect = new Rect();
        mDayTextRect.left = 0;
        mDayTextRect.top = 0;
        mDayTextRect.bottom = height - dpToPx(mBottomTextHeight);
        mDayTextRect.right = width;
    }


    private Rect mBottomTextRect;

    private void calculateBottomTextBound(int width, int height) {
        mBottomTextRect = new Rect();
        mBottomTextRect.left = 0;
        mBottomTextRect.top = height - mBottomTextHeight - dpToPx(3);
        mBottomTextRect.bottom = height - dpToPx(3);
        mBottomTextRect.right = width;
    }

    private float mCircleX;
    private float mCircleY;
    private float mCircleRadius;


    /**
     * 圆点 范围
     *
     * @param width
     * @param height
     */
    private void calculateCircleBound(int width, int height) {
        mCircleX = width / 2;
        mCircleY = (height - dpToPx(mBottomTextHeight)) / 2;
        mCircleRadius = mCircleY - dpToPx(3);
    }


    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }

    private int spToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, dp, getResources().getDisplayMetrics()
        );
    }

    /**
     * 是否 选中
     *
     * @param b
     */
    public void setChecked(boolean b) {
        mIsChecked = b;
        postInvalidate();
    }


    public boolean isStrat() {
        return mIsStrat;
    }


    public boolean isEnd() {
        return mIsEnd;
    }


    public boolean isLeft() {
        return mIsLeft;
    }


    public boolean isRight() {
        return mIsRight;
    }


    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public String toString() {
        return "DayView{" +
                "mIsStrat=" + mIsStrat +
                ", mIsEnd=" + mIsEnd +
                ", mIsLeft=" + mIsLeft +
                ", mIsRight=" + mIsRight +
                ", mIsChecked=" + mIsChecked +
                '}';
    }
}
