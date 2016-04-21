package com.easemob.qa.upload.model;

public enum Option {

    LOAD(0, "load from database"), UPLOAD(1, "upload to remote server"), BOTH(2,
            "load firstly and then upload");

    private int code;

    private String description;

    private Option(int num, String desc) {
        this.code = num;
        this.description = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.description;
    }

    public static Option valueOf(int code) {
        switch (code) {
            case 0:
                return LOAD;
            case 1:
                return UPLOAD;
            case 2:
                return BOTH;
            default:
                return null;
        }
    }

}
