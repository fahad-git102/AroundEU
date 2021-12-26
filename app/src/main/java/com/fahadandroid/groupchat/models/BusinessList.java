package com.fahadandroid.groupchat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessList implements Parcelable {
    String key, name, countryId;
    long timeStamp;
    boolean deleted = false;

    public BusinessList() {
    }

    protected BusinessList(Parcel in) {
        key = in.readString();
        name = in.readString();
        timeStamp = in.readLong();
    }

    public static final Creator<BusinessList> CREATOR = new Creator<BusinessList>() {
        @Override
        public BusinessList createFromParcel(Parcel in) {
            return new BusinessList(in);
        }

        @Override
        public BusinessList[] newArray(int size) {
            return new BusinessList[size];
        }
    };

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeLong(timeStamp);
    }
}
