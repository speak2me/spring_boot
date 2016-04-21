package com.easemob.qa.upload.model;

import java.util.Date;

import lombok.Data;

@Data
public class Event {

    //    private int easemobTenantId;

    private String rulaiTenantId;

    private Date timeBeginDate;

    private Date timeEndDate;

    private String accessToken;

    private int option;
}
