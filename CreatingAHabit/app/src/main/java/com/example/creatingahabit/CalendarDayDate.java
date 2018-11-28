package com.example.creatingahabit;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

public class CalendarDayDate implements Parcelable {
    Date date;
    Long time;

    public CalendarDayDate(Long t){
        time = t;
    }

    protected CalendarDayDate(Parcel in) {
    }

    public static final Creator<CalendarDayDate> CREATOR = new Creator<CalendarDayDate>() {
        @Override
        public CalendarDayDate createFromParcel(Parcel in) {
            return new CalendarDayDate(in);
        }

        @Override
        public CalendarDayDate[] newArray(int size) {
            return new CalendarDayDate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(time);
    }

    private void readFromParcel(Parcel in) {
        // Read Long value and convert to date
        date = new Date(in.readLong());

    }
}
