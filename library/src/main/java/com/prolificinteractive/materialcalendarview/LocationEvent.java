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

    public LocationEvent(int[] location, boolean isStart, boolean isEnd) {
        this.location = location;
        this.isStart = isStart;
        this.isEnd = isEnd;
    }

    @Override
    public String toString() {
        return "LocationEvent{" +
                "location=" + Arrays.toString(location) +
                ", isStart=" + isStart +
                ", isEnd=" + isEnd +
                '}';
    }
}
