package com.easemob.qa.upload.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easemob.qa.upload.model.Event;
import com.easemob.qa.upload.model.Option;
import com.easemob.qa.upload.model.Status;
import com.easemob.qa.upload.worker.FileWorker;
import com.easemob.qa.upload.worker.LoadWorker;
import com.easemob.qa.upload.worker.UploadWorker;

@Service
public class PoolService {

    @Autowired
    DataService operator;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(IConst.POOL_SIZE);

    private static ExecutorService filePool = Executors.newFixedThreadPool(15);

    private static ExecutorService uploadPool = Executors.newFixedThreadPool(10);

    private static ExecutorService requestPool = Executors.newFixedThreadPool(1);

    public Future<Boolean> execute(LoadWorker worker) {
        Future<Boolean> result = threadPool.submit(worker);
        return result;
    }

    public Future<Boolean> compress(FileWorker worker) {
        Future<Boolean> result = filePool.submit(worker);
        return result;
    }

    public Future<Boolean> upload(UploadWorker worker) {
        Future<Boolean> result = uploadPool.submit(worker);
        return result;
    }

    public void deal(int easemobTenantId, Event param) {
        requestPool.execute(new Runnable() {

            @Override
            public void run() {
                Option option = Option.valueOf(param.getOption());
                Status currentStatus = null;
                switch (option) {
                    case LOAD:
                        operator.load(easemobTenantId, param);
                        break;
                    case UPLOAD:
                        operator.upload(param);
                        currentStatus = operator.report(param.getRulaiTenantId()).getPhrase();
                        if (currentStatus == Status.UPLOADED) {
                            operator.notify(param.getRulaiTenantId());
                        }
                        break;
                    case BOTH:
                        operator.load(easemobTenantId, param);
                        currentStatus = operator.report(param.getRulaiTenantId()).getPhrase();
                        if (currentStatus == Status.LOADED) {
                            operator.upload(param);
                        }
                        currentStatus = operator.report(param.getRulaiTenantId()).getPhrase();
                        if (currentStatus == Status.UPLOADED) {
                            operator.notify(param.getRulaiTenantId());
                        }
                        break;
                    default:
                        break;
                }

            }
        });
    }

}
