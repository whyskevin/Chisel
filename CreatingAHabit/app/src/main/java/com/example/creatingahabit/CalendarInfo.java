//used this website to generate this class:
//http://www.parcelabler.com/
package com.example.creatingahabit;

import android.os.Parcel;
import android.os.Parcelable;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;

public class CalendarInfo implements Parcelable {

    String habitName;
    private ArrayList<CalendarDay> completed;
    private ArrayList<CalendarDay> notCompleted;

    public CalendarInfo(String habitName, ArrayList<CalendarDay> completed, ArrayList<CalendarDay> notCompleted) {
        this.habitName = habitName;
        this.completed = completed;
        this.notCompleted = notCompleted;
    }

    public ArrayList<CalendarDay> getCompleted() {
        return completed;
    }

    public ArrayList<CalendarDay> getNotCompleted() {
        return notCompleted;
    }

    public String getHabitName() {
        return habitName;
    }

    protected CalendarInfo(Parcel in) {
        habitName = in.readString();
        if (in.readByte() == 0x01) {
            completed = new ArrayList<CalendarDay>();
            in.readList(completed, CalendarDay.class.getClassLoader());
        } else {
            completed = null;
        }
        if (in.readByte() == 0x01) {
            notCompleted = new ArrayList<CalendarDay>();
            in.readList(notCompleted, CalendarDay.class.getClassLoader());
        } else {
            notCompleted = null;
        }
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(habitName);
        if (completed == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(completed);
        }
        if (notCompleted == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(notCompleted);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CalendarInfo> CREATOR = new Parcelable.Creator<CalendarInfo>() {
        @Override
        public CalendarInfo createFromParcel(Parcel in) {
            return new CalendarInfo(in);
        }

        @Override
        public CalendarInfo[] newArray(int size) {
            return new CalendarInfo[size];
        }
    };
}