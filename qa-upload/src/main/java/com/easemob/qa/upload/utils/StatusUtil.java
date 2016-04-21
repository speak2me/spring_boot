package com.easemob.qa.upload.utils;

import java.util.concurrent.ConcurrentHashMap;

import com.easemob.qa.upload.model.Status;

public class StatusUtil {

    private static ConcurrentHashMap<String, Status> dataStatus = new ConcurrentHashMap<String, Status>(
            16);

    public static Status checkStatus(String appKey) {
        if (dataStatus.containsKey(appKey)) {
            return dataStatus.get(appKey);
        }
        return null;
    }

    public void recordStatus(String appKey, Status status) {
        dataStatus.put(appKey, status);
    }

}
