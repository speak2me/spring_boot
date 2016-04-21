package com.easemob.qa.upload.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.easemob.qa.upload.model.Event;
import com.easemob.qa.upload.model.Option;
import com.easemob.qa.upload.model.RunStatus;
import com.easemob.qa.upload.model.Status;
import com.easemob.qa.upload.service.DataService;
import com.easemob.qa.upload.service.PoolService;

@RestController
@RequestMapping("/v1/tenants")
public class UploadController {

    @Autowired
    PoolService threadPool;

    @Autowired
    DataService operator;

    @RequestMapping(value = "/{tenantId}/easemob/dialog/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void opearte(HttpServletRequest request,
            @PathVariable("tenantId") Integer easemobTenantId, @RequestBody Event param) {
        //        threadPool.deal(easemobTenantId, param);
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

    @RequestMapping(value = "/status/{rulaiTenantId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String check(HttpServletRequest request,
            @PathVariable("rulaiTenantId") String rulaiTenantId) {
        JSONObject object = new JSONObject();
        RunStatus status = operator.report(rulaiTenantId);
        try {
            object.put("tenantId", rulaiTenantId);
            object.put("phrase", status.getPhrase().getDesc());
            object.put("upload progress", status.getPercent());
            object.put("start stamp", status.getBeginTime());
        } catch (Exception e) {}
        return object.toString();
    }
}
