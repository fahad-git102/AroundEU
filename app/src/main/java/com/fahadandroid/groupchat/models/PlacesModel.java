package com.fahadandroid.groupchat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PlacesModel implements Parcelable{

    String key, description, uid, imageUrl, category, status, country;
    long timeStamp;
    LocationModel location;

    public PlacesModel(){}

    protected PlacesModel(Parcel in) {
        key = in.readString();
        description = in.readString();
        uid = in.readString();
        imageUrl = in.readString();
        category = in.readString();
        status = in.readString();
        country = in.readString();
        timeStamp = in.readLong();
    }

    public static final Creator<PlacesModel> CREATOR = new Creator<PlacesModel>() {
        @Override
        public PlacesModel createFromParcel(Parcel in) {
            return new PlacesModel(in);
        }

        @Override
        public PlacesModel[] newArray(int size) {
            return new PlacesModel[size];
        }
    };

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getKey() {
        return key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(description);
        parcel.writeString(uid);
        parcel.writeString(imageUrl);
        parcel.writeString(category);
        parcel.writeString(status);
        parcel.writeString(country);
        parcel.writeLong(timeStamp);
    }
}
