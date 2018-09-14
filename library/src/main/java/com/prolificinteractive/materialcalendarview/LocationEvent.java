package com.prolificinteractive.materialcalendarview;

import java.util.Arrays;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/8/31
 * Description :
 */
public class LocationEvent {
    private int location[];
    private boolean isStart;
    private boolean isEnd;
    private boolean isLeft;
    private boolean isRight;
    private boolean isTheSameDay;
    private int dayViewWidth;

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public boolean isRight() {
        return isRight;
    }


    public boolean isTheSameDay() {
        return isTheSameDay;
    }

    public void setTheSameDay(boolean theSameDay) {
        isTheSameDay = theSameDay;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public int getDayViewWidth() {
        return dayViewWidth;
    }

    public void setDayViewWidth(int dayViewWidth) {
        this.dayViewWidth = dayViewWidth;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public int[] getLocation() {
        return location;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public LocationEvent(int[] location, boolean isStart, boolean isEnd, boolean isLeft, boolean isRight,boolean isTheSameDay,int dayViewWidth ) {
        this.location = location;
        this.isStart = isStart;
        this.isEnd = isEnd;
        this.isLeft = isLeft;
        this.isRight = isRight;
        this.isTheSameDay = isTheSameDay;
        this.dayViewWidth = dayViewWidth;
    }

    @Override
    public String toString() {
        return "LocationEvent{" +
                "location=" + Arrays.toString(location) +
                ", isStart=" + isStart +
                ", isEnd=" + isEnd +
                ", isLeft=" + isLeft +
                ", isRight=" + isRight +
                ", dayViewWidth=" + dayViewWidth +
                '}';
    }
}
