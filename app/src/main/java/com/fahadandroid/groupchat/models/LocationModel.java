package com.fahadandroid.groupchat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationModel implements Parcelable {
    String address;
    double latitude, longitude;

    public LocationModel(){}

    protected LocationModel(Parcel in) {
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
