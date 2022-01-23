package com.fahadandroid.groupchat.models;

import java.util.HashMap;
import java.util.List;

public class GroupsModel {
    long createdOn;
    String joined;
    String key, businessKey, name, createdBy, category;
    String pincode;
    List<String> categoryList, fileUrls;
    HashMap<String, MessagesModel> messages;
    boolean deleted;
    List<String> approvedMembers, pendingMembers;

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
