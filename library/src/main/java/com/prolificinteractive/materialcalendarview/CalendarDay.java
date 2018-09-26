package com.prolificinteractive.materialcalendarview;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * An imputable representation of a day on a calendar
 * <p>
 * <p>
 * <p>
 * 我发现这里面的 Calendar和 date  会出现与  int year, int month, int day 不对应的情况,
 * 所以  暂时  只向调用方  提供  毫秒  由调用端  自行进行 转化
 */
public final class CalendarDay implements Parcelable {


    /**
     * 时间段开始
     */
    private boolean mIsStrat = false;
    /**
     * 时间段结束
     */
    private boolean mIsEnd = false;

    private boolean mIsEndChecked = false;


    /**
     * 同一天
     */
    private boolean mIsTheSameDay = false;


    public boolean isEndChecked() {
        return mIsEndChecked;
    }

    public void setEndChecked(boolean endChecked) {
        mIsEndChecked = endChecked;
    }

    public boolean isStrat() {
        return mIsStrat;
    }

    public void setStrat(boolean strat) {
        mIsStrat = strat;
    }

    public boolean isEnd() {
        return mIsEnd;
    }

    public void setEnd(boolean end) {
        mIsEnd = end;
    }


    public boolean isTheSameDay() {
        return mIsTheSameDay;
    }

    public void setTheSameDay(boolean theSameDay) {
        mIsTheSameDay = theSameDay;
    }

    /**
     * Get a new instance set to today
     *
     * @return CalendarDay set to today's date
     */
    @NonNull
    public static CalendarDay today() {
        return from(CalendarUtils.getInstance());
    }

    /**
     * Get a new instance set to the specified day
     *
     * @param year  new instance's year
     * @param month new instance's month as defined by {@linkplain java.util.Calendar}
     * @param day   new instance's day of month
     * @return CalendarDay set to the specified date
     */
    @NonNull
    public static CalendarDay from(int year, int month, int day) {
        return new CalendarDay(year, month, day);
    }

    /**
     * Get a new instance set to the specified day
     *
     * @param calendar {@linkplain Calendar} to pull date information from. Passing null will return null
     * @return CalendarDay set to the specified date
     */
    public static CalendarDay from(@Nullable Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return from(
                CalendarUtils.getYear(calendar),
                CalendarUtils.getMonth(calendar),
                CalendarUtils.getDay(calendar)
        );
    }

    /**
     * Get a new instance set to the specified day
     *
     * @param date long to pull date information from.
     * @return CalendarDay set to the specified date
     */
    public static CalendarDay from(long date) {
        final Calendar instance = CalendarUtils.getInstance();
        instance.setTimeInMillis(date);
        return from(instance);
    }

    private final int year;
    private final int month;
    private final int day;

    /**
     * Cache for calls to {@linkplain #getCalendar()}
     */
    private transient Calendar _calendar;

    /**
     * Cache for calls to {@linkplain #getDate()}
     */
    private transient Date _date;

    /**
     * Initialized to the current day
     *
     * @see CalendarDay#today()
     */
    @Deprecated
    public CalendarDay() {
        this(CalendarUtils.getInstance());
    }

    /**
     * @param calendar source to pull date information from for this instance
     * @see CalendarDay#from(Calendar)
     */
    @Deprecated
    public CalendarDay(Calendar calendar) {
        this(
                CalendarUtils.getYear(calendar),
                CalendarUtils.getMonth(calendar),
                CalendarUtils.getDay(calendar)
        );
    }

    /**
     * @param year  new instance's year
     * @param month new instance's month as defined by {@linkplain java.util.Calendar}
     * @param day   new instance's day of month
     * @see CalendarDay#from(Calendar)
     */
    @Deprecated
    public CalendarDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Get the year
     *
     * @return the year for this day
     */
    int getYear() {
        return year;
    }

    /**
     * Get the month, represented by values from {@linkplain Calendar}
     *
     * @return the month of the year as defined by {@linkplain Calendar}
     */

    int getMonth() {
        return month;
    }


    /**
     * Get the day
     *
     * @return the day of the month for this day
     */
    int getDay() {
        return day;
    }

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public long getTimeInMillis() {
        try {
            return mSimpleDateFormat.parse(String.format("%d-%d-%d", year, month + 1, day)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get this day as a {@linkplain Date}
     *
     * @return a date with this days information
     */
    @NonNull
    Date getDate() {
        if (_date == null) {
            _date = getCalendar().getTime();
        }
        return _date;
    }

    /**
     * Get this day as a {@linkplain Calendar}
     *
     * @return a new calendar instance with this day information
     */
    @NonNull
    Calendar getCalendar() {
        //if (_calendar == null) {
        _calendar = CalendarUtils.getInstance();
        copyTo(_calendar);
        //}
        return _calendar;
    }

    void copyToMonthOnly(@NonNull Calendar calendar) {
        calendar.set(year, month, 1);
    }

    /**
     * Copy this day's information to the given calendar instance
     *
     * @param calendar calendar to set date information to
     */
    public void copyTo(@NonNull Calendar calendar) {
        calendar.set(year, month, day);
    }

    /**
     * Determine if this day is within a specified range
     *
     * @param minDate the earliest day, may be null
     * @param maxDate the latest day, may be null
     * @return true if the between (inclusive) the min and max dates.
     */
    public boolean isInRange(@Nullable CalendarDay minDate, @Nullable CalendarDay maxDate) {
        return !(minDate != null && minDate.isAfter(this)) &&
                !(maxDate != null && maxDate.isBefore(this));
    }

    /**
     * Determine if this day is before the given instance
     *
     * @param other the other day to test
     * @return true if this is before other, false if equal or after
     */
    public boolean isBefore(@NonNull CalendarDay other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }
        if (year == other.year) {
            return ((month == other.month) ? (day < other.day) : (month < other.month));
        } else {
            return year < other.year;
        }
    }

    /**
     * Determine if this day is after the given instance
     *
     * @param other the other day to test
     * @return true if this is after other, false if equal or before
     */
    public boolean isAfter(@NonNull CalendarDay other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }

        if (year == other.year) {
            return (month == other.month) ? (day > other.day) : (month > other.month);
        } else {
            return year > other.year;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CalendarDay that = (CalendarDay) o;

        return day == that.day && month == that.month && year == that.year;
    }

    @Override
    public int hashCode() {
        return hashCode(year, month, day);
    }

    private static int hashCode(int year, int month, int day) {
        //Should produce hashes like "20150401"
        return (year * 10000) + (month * 100) + day;
    }

    @Override
    public String toString() {
        return "CalendarDay{" + year + "-" + month + "-" + day + "-" + isStrat() + "-" + isEnd() + "}";
    }

    /*
     * Parcelable Stuff
     */


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mIsStrat ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsEnd ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsEndChecked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
    }

    protected CalendarDay(Parcel in) {
        this.mIsStrat = in.readByte() != 0;
        this.mIsEnd = in.readByte() != 0;
        this.mIsEndChecked = in.readByte() != 0;
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
    }

    public static final Creator<CalendarDay> CREATOR = new Creator<CalendarDay>() {
        @Override
        public CalendarDay createFromParcel(Parcel source) {
            return new CalendarDay(source);
        }

        @Override
        public CalendarDay[] newArray(int size) {
            return new CalendarDay[size];
        }
    };
}
