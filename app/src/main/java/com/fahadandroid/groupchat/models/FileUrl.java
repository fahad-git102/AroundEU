package com.fahadandroid.groupchat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FileUrl implements Parcelable {
    String url, name;

    public FileUrl() {
    }

    public FileUrl(String url, String name) {
        this.url = url;
        this.name = name;
    }

    protected FileUrl(Parcel in) {
        url = in.readString();
        name = in.readString();
    }

    public static final Creator<FileUrl> CREATOR = new Creator<FileUrl>() {
        @Override
        public FileUrl createFromParcel(Parcel in) {
            return new FileUrl(in);
        }

        @Override
        public FileUrl[] newArray(int size) {
            return new FileUrl[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(name);
    }
}
