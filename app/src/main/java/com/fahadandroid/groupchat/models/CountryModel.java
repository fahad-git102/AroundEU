package com.fahadandroid.groupchat.models;

public class CountryModel {
    String key, countryName, pincode;
    boolean isDeleted = false;

    public CountryModel() {
    }

    public CountryModel(String countryName, String pincode) {
        this.countryName = countryName;
        this.pincode = pincode;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    @Override
    public String toString() {
        return countryName;
    }
}
