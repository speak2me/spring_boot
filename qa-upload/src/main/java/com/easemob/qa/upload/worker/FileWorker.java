package com.easemob.qa.upload.worker;

import java.util.concurrent.Callable;

import com.easemob.qa.upload.utils.FileUtil;

public class FileWorker implements Callable<Boolean> {

    private String dataPath;

    private String rulaiTenantId;

    private String content;

    private int thread_index;

    public FileWorker(String path, String tenantId, int thread_index, String content) {
        this.rulaiTenantId = tenantId;
        this.thread_index = thread_index;
        this.content = content;
        this.dataPath = path;
    }

    @Override
    public Boolean call() throws Exception {
        FileUtil.compress(dataPath, rulaiTenantId, thread_index, content);
        return true;
    }

}
