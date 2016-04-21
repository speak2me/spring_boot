package com.easemob.qa.upload.model;

import lombok.Data;

@Data
public class RunStatus {

    private String rulaiTenantId;

    private float percent;//for upload only

    private Status phrase;

    private long beginTime;

    private long endTime;
}
