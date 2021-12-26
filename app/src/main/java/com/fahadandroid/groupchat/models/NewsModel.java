package com.fahadandroid.groupchat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsModel implements Parcelable{
    String description, title, imageUrl, uid, key;
    long timeStamp;

    public NewsModel() {
    }

    public NewsModel(String description, String imageUrl, String uid, long timeStamp) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.timeStamp = timeStamp;
    }

    protected NewsModel(Parcel in) {
        description = in.readString();
        title = in.readString();
        imageUrl = in.readString();
        uid = in.readString();
        key = in.readString();
        timeStamp = in.readLong();
    }

    public static final Creator<NewsModel> CREATOR = new Creator<NewsModel>() {
        @Override
        public NewsModel createFromParcel(Parcel in) {
            return new NewsModel(in);
        }

        @Override
        public NewsModel[] newArray(int size) {
            return new NewsModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
        parcel.writeString(description);
        parcel.writeString(title);
        parcel.writeString(imageUrl);
        parcel.writeString(uid);
        parcel.writeString(key);
        parcel.writeLong(timeStamp);
    }
}
