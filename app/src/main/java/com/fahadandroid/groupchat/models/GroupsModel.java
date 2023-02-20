package com.fahadandroid.groupchat.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;

public class GroupsModel implements Parcelable {
    long createdOn;
    String joined;
    String key, businessKey, name, createdBy, category;
    String pincode;
    List<String> categoryList, fileUrls;
    HashMap<String, MessagesModel> messages;
    boolean deleted;
    List<String> approvedMembers, pendingMembers;
    HashMap<String, Long> unReadCounts;

    protected GroupsModel(Parcel in) {
        createdOn = in.readLong();
        joined = in.readString();
        key = in.readString();
        businessKey = in.readString();
        name = in.readString();
        createdBy = in.readString();
        category = in.readString();
        pincode = in.readString();
        categoryList = in.createStringArrayList();
        fileUrls = in.createStringArrayList();
        deleted = in.readByte() != 0;
        approvedMembers = in.createStringArrayList();
        pendingMembers = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createdOn);
        dest.writeString(joined);
        dest.writeString(key);
        dest.writeString(businessKey);
        dest.writeString(name);
        dest.writeString(createdBy);
        dest.writeString(category);
        dest.writeString(pincode);
        dest.writeStringList(categoryList);
        dest.writeStringList(fileUrls);
        dest.writeByte((byte) (deleted ? 1 : 0));
        dest.writeStringList(approvedMembers);
        dest.writeStringList(pendingMembers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupsModel> CREATOR = new Creator<GroupsModel>() {
        @Override
        public GroupsModel createFromParcel(Parcel in) {
            return new GroupsModel(in);
        }

        @Override
        public GroupsModel[] newArray(int size) {
            return new GroupsModel[size];
        }
    };

    public HashMap<String, Long> getUnReadCounts() {
        return unReadCounts;
    }

    public void setUnReadCounts(HashMap<String, Long> unReadCounts) {
        this.unReadCounts = unReadCounts;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public GroupsModel() {
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public GroupsModel(long createdOn, String businessKey, String name, String createdBy, String pincode, List<String> categoryList) {
        this.createdOn = createdOn;
        this.businessKey = businessKey;
        this.name = name;
        this.categoryList = categoryList;
        this.createdBy = createdBy;
        this.pincode = pincode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public HashMap<String, MessagesModel> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, MessagesModel> messages) {
        this.messages = messages;
    }

    public List<String> getApprovedMembers() {
        return approvedMembers;
    }

    public void setApprovedMembers(List<String> approvedMembers) {
        this.approvedMembers = approvedMembers;
    }

    public List<String> getPendingMembers() {
        return pendingMembers;
    }

    public void setPendingMembers(List<String> pendingMembers) {
        this.pendingMembers = pendingMembers;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
