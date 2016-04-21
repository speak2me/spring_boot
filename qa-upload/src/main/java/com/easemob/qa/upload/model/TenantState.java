package com.easemob.qa.upload.model;

import java.util.Date;

import lombok.Data;

@Data
public class TenantState {

    private String tenantId;

    private Short state;

    private Date createTime;

    private Date updateTime;
}
