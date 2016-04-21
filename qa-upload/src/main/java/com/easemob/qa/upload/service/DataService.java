package com.easemob.qa.upload.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.easemob.qa.upload.model.Event;
import com.easemob.qa.upload.model.RunStatus;
import com.easemob.qa.upload.model.Status;
import com.easemob.qa.upload.model.TenantState;
import com.easemob.qa.upload.persistence.entity.ServicesessionCriteria;
import com.easemob.qa.upload.persistence.mapper.ChatmessageMapper;
import com.easemob.qa.upload.persistence.mapper.ServicesessionMapper;
import com.easemob.qa.upload.persistence.mapper.SessionIdMapper;
import com.easemob.qa.upload.worker.LoadWorker;
import com.easemob.qa.upload.worker.UploadWorker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataService {

    @Autowired
    private ChatmessageMapper chatmessageMapper;

    @Autowired
    private ServicesessionMapper sessionMapper;

    @Autowired
    private SessionIdMapper idMapper;

    @Autowired
    private PoolService poolService;

    @Value("${upload_uri}")
    private String uploadUri;

    @Value("${upload_finished_uri}")
    private String uploadFinishedUri;

    @Value("${data_path}")
    private String dataPath;

    private static ConcurrentHashMap<String, RunStatus> runRocord = new ConcurrentHashMap<String, RunStatus>();

    public void load(int easemobTenantId, Event parameter) {
        String rulaiTenantId = parameter.getRulaiTenantId();
        if (!runRocord.containsKey(rulaiTenantId)) {
            RunStatus status = new RunStatus();
            status.setPercent(0f);
            status.setPhrase(Status.LOADING);
            status.setRulaiTenantId(rulaiTenantId);
            status.setBeginTime(System.currentTimeMillis());
            runRocord.put(rulaiTenantId, status);
        } else {
            return;//repeat request
        }
        log.info("easemob_tenant_id:{} is loading from db!", easemobTenantId);
        Date start = parameter.getTimeBeginDate();
        Date stop = parameter.getTimeEndDate();
        ServicesessionCriteria criteria = new ServicesessionCriteria();
        criteria.createCriteria().andTenantidEqualTo(easemobTenantId)
                .andStartdatetimeGreaterThan(start).andStopdatetimeLessThan(stop);
        int count = sessionMapper.countByExample(criteria);
        log.info("there exists {}'s session", count);
        int total = count % IConst.SESSION_PAGE_SIZE == 0 ? (count / IConst.SESSION_PAGE_SIZE)
                : (count / IConst.SESSION_PAGE_SIZE + 1);
        boolean flag = true;
        for (int i = 0; i < total; i++) {
            ArrayList<Future<Boolean>> signalList = new ArrayList<Future<Boolean>>();
            RowBounds rowBounds = new RowBounds(i * IConst.SESSION_PAGE_SIZE,
                    IConst.SESSION_PAGE_SIZE);
            //            List<Servicesession> sessionList = sessionMapper.selectByExampleWithRowbounds(criteria,
            //                    rowBounds);
            List<String> sessionList = idMapper.selectByExampleWithRowbounds(criteria, rowBounds);
            int threadCount = (sessionList.size() % IConst.CHAT_PAGE_SIZE == 0)
                    ? (sessionList.size() % IConst.CHAT_PAGE_SIZE)
                    : (sessionList.size() / IConst.CHAT_PAGE_SIZE + 1);
            for (int j = 0; j < threadCount && j < IConst.POOL_SIZE; j++) {
                LoadWorker worker = new LoadWorker(j, dataPath, rulaiTenantId, easemobTenantId,
                        sessionList, chatmessageMapper, poolService);
                Future<Boolean> signal = poolService.execute(worker);
                signalList.add(signal);
            }
            for (Future<Boolean> one : signalList) {
                try {
                    flag &= one.get();
                    if (flag == false) break;
                } catch (Exception e) {
                    log.error(e.getMessage(), e.getCause());
                }
            }
            if (flag) {
                continue;
            } else {
                break;
            }
        }
        runRocord.get(rulaiTenantId).setEndTime(System.currentTimeMillis());
        if (flag) {
            runRocord.get(rulaiTenantId).setPhrase(Status.LOADED);
            log.info("easemob_tenant_id:{} loaded successfully!", easemobTenantId);
        } else {
            runRocord.get(rulaiTenantId).setPhrase(Status.UNKNOWN);
            log.info("easemob_tenant_id:{} loaded unsuccessfully!", easemobTenantId);
        }
    }

    public void upload(Event parameter) {
        log.info("rulai_tenant_id:{} is uploading to rulai!", parameter.getRulaiTenantId());
        if (!runRocord.containsKey(parameter.getRulaiTenantId())) {
            RunStatus status = new RunStatus();
            status.setBeginTime(System.currentTimeMillis());
            status.setPercent(0);
            status.setPhrase(Status.UPLOADING);
            status.setRulaiTenantId(parameter.getRulaiTenantId());
            runRocord.put(parameter.getRulaiTenantId(), status);
        } else {
            runRocord.get(parameter.getRulaiTenantId()).setPhrase(Status.UPLOADING);
        }
        String path = dataPath + "/" + parameter.getRulaiTenantId();
        File tenantD = new File(path);
        if (!tenantD.exists() && !tenantD.isDirectory()) {
            return;
        }
        String[] allFiles = tenantD.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        });
        List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>();
        if (allFiles != null) {
            for (String file : allFiles) {
                UploadWorker worker = new UploadWorker(uploadUri, path, file,
                        parameter.getAccessToken(), parameter.getRulaiTenantId());
                resultList.add(poolService.upload(worker));
            }
        }
        boolean flag = true;
        for (Future<Boolean> future : resultList) {
            try {
                flag &= future.get();
            } catch (InterruptedException | ExecutionException e) {}
        }
        if (flag) {
            runRocord.get(parameter.getRulaiTenantId()).setEndTime(System.currentTimeMillis());
            runRocord.get(parameter.getRulaiTenantId()).setPercent(1.0f);
            runRocord.get(parameter.getRulaiTenantId()).setPhrase(Status.UPLOADED);
            log.info("rulai_tenant_id:{} uploaded successfully!", parameter.getRulaiTenantId());
        } else {
            log.warn("it seems that some file failed uploading!");
        }

    }

    public void notify(String rulaiTenantId) {
        RestTemplate template = new RestTemplate();
        String serverUri = uploadFinishedUri.replace("{tenantId}", rulaiTenantId);
        TenantState state = new TenantState();
        state.setTenantId(rulaiTenantId);
        state.setState((short) 1);
        state.setCreateTime(new Date());
        template.put(serverUri, state);
    }

    public RunStatus report(String rulaiTenantId) {
        if (runRocord.containsKey(rulaiTenantId)) {
            return runRocord.get(rulaiTenantId);
        } else {
            return new RunStatus();
        }
    }
}
