package com.easemob.qa.upload.worker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UploadWorker implements Callable<Boolean> {

    String uri;

    String parentFile;

    String childFile;

    String token;

    String rulaiTenantId;

    public UploadWorker(String remoteUri, String parentPath, String childPath, String token,
            String rulaiTenantId) {
        this.uri = remoteUri;
        this.parentFile = parentPath;
        this.childFile = childPath;
        this.token = token;
        this.rulaiTenantId = rulaiTenantId;
    }

    @Override
    public Boolean call() throws Exception {
        boolean flag = true;
        try {
            post(uri, parentFile, childFile);
        } catch (Exception e) {
            log.error("{} upload failed reason:{} ", rulaiTenantId, e.getMessage());
            flag = false;
        }
        return flag;

    }

    private void post(String url, String parentPath, String theFile) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
        paramMap.add("file", new FileSystemResource(parentPath + "/" + theFile));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer" + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(
                paramMap, headers);
        Map<String, String> pathValueMap = new HashMap<String, String>();
        pathValueMap.put("tenantId", rulaiTenantId);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class, pathValueMap);
    }

}
