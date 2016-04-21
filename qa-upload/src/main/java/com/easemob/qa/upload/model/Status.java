package com.easemob.qa.upload.model;

public enum Status {

    LOADING("is loading from database"), LOADED("load finished"), UPLOADING(
            "is uploading to rulai"), UPLOADED("upload finished"), UNKNOWN("something is wrong");

    private String action;

    private Status(String desc) {
        this.action = desc;
    }

    public String getDesc() {
        return action;
    }
}
