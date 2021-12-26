package com.fahadandroid.groupchat.models;

import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class ComapnyTimeScheduledModel {
    String morningFrom, morningTo, noonFrom, noonTo, description, key, companyId, uid;
    List<MaterialDayPicker.Weekday> selectedDays;

    public List<MaterialDayPicker.Weekday> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<MaterialDayPicker.Weekday> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMorningFrom() {
        return morningFrom;
    }

    public void setMorningFrom(String morningFrom) {
        this.morningFrom = morningFrom;
    }

    public String getMorningTo() {
        return morningTo;
    }

    public void setMorningTo(String morningTo) {
        this.morningTo = morningTo;
    }

    public String getNoonFrom() {
        return noonFrom;
    }

    public void setNoonFrom(String noonFrom) {
        this.noonFrom = noonFrom;
    }

    public String getNoonTo() {
        return noonTo;
    }

    public void setNoonTo(String noonTo) {
        this.noonTo = noonTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
