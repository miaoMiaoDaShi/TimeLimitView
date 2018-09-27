package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/9/27
 * Description :配合日历使用的
 */
public class TipView extends android.support.v7.widget.AppCompatTextView {

    @IntDef(value = {
            Where.WHERE_LEFT,
            Where.WHERE_CENTER,
            Where.WHERE_RIGHT,
    })
    public @interface Where {
        int WHERE_LEFT = 1;
        int WHERE_CENTER = 2;
        int WHERE_RIGHT = 3;
    }


    private int mWhere = Where.WHERE_CENTER;

    /**
     * 是否已经调整了大小
     */
    private boolean mIsMod = false;

    /**
     * dayView的宽度
     */
    private int mDayViewWidth;

    private RectF mRectF = new RectF();
    private int mShadowRadius;


    private int mBackgroundColor = Color.parseColor("#222222");
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TipView(Context context) {
        this(context, null);
    }

    public TipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setDayViewWidth(int dayViewWidth) {
        mDayViewWidth = dayViewWidth;
    }

    private void init() {

        setGravity(Gravity.CENTER);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mShadowRadius = dpToPx(5);
        mDayViewWidth = dpToPx(50);

        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setShadowLayer(mShadowRadius, 0, 0, Color.parseColor("#50222222"));
    }

    /**
     * 设置三角形的位置
     *
     * @param where
     */
    public void setWhere(@Where int where) {
        mWhere = where;
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final Rect rect = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), rect);
        setWidth(rect.width() + dpToPx(20));
        setHeight(rect.height() + dpToPx(30));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        super.onDraw(canvas);
    }

    /**
     * 背景绘制
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {

        mRectF.left = getPaddingTop() + mShadowRadius;
        mRectF.top = getPaddingLeft() + mShadowRadius + dpToPx(5);
        mRectF.right = getWidth() - getPaddingRight() - mShadowRadius;
        mRectF.bottom = getHeight() - getPaddingBottom() - mShadowRadius - dpToPx(5);
        // canvas.drawRoundRect(mRectF, dpToPx(5), dpToPx(5), mBackgroundPaint);


        PointF pointA = new PointF();
        PointF pointB = new PointF();
        PointF pointC = new PointF();
        switch (mWhere) {
            case Where.WHERE_LEFT:
                pointA.x = mDayViewWidth / 2 - dpToPx(5);
                pointA.y = mRectF.bottom;

                pointB.x = mDayViewWidth / 2 + dpToPx(5);
                pointB.y = mRectF.bottom;

                pointC.x = mDayViewWidth / 2;
                pointC.y = mRectF.bottom + dpToPx(5);
                break;
            case Where.WHERE_CENTER:
                pointA.x = getWidth() / 2 - dpToPx(5);
                pointA.y = mRectF.bottom;

                pointB.x = getWidth() / 2 + dpToPx(5);
                pointB.y = mRectF.bottom;

                pointC.x = getWidth() / 2;
                pointC.y = mRectF.bottom + dpToPx(5);
                break;
            case Where.WHERE_RIGHT:
                pointA.x = mRectF.right - mDayViewWidth / 2 ;
                pointA.y = mRectF.bottom;

                pointB.x = mRectF.right - mDayViewWidth / 2+dpToPx(10) ;
                pointB.y = mRectF.bottom;

                pointC.x = mRectF.right - mDayViewWidth / 2+dpToPx(5);
                pointC.y = mRectF.bottom + dpToPx(5);
                break;
        }

        final Path path = new Path();
        path.addRoundRect(mRectF, dpToPx(5), dpToPx(5), Path.Direction.CW);
        path.moveTo(pointA.x, pointA.y);
        path.lineTo(pointB.x, pointB.y);
        path.lineTo(pointC.x, pointC.y);
        canvas.drawPath(path, mBackgroundPaint);
    }


    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }
}
