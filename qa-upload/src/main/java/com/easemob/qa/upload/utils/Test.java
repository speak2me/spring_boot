package com.easemob.qa.upload.utils;

import java.io.File;
import java.io.FilenameFilter;
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

public class Test {

    public void upload(String dataPath, String rulaiTenantId) {
        String path = dataPath + "/" + rulaiTenantId;
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
        if (allFiles != null) {
            for (String file : allFiles) {
                //                RestTemplate template = new RestTemplate();
                //                MultiValueMap<String, Object> formMap = new LinkedMultiValueMap<String, Object>();
                //                formMap.add("file", new FileSystemResource(path + "/" + file));
                //                String response = template.postForObject(
                //                        "http://http://52.24.207.146:8989/v1/tenants/easemob_test/qa/dialog",
                //                        formMap, String.class);
                //                System.out.println(response);

                RestTemplate template = new RestTemplate();
                MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
                FileSystemResource resource = new FileSystemResource(new File(path + "/" + file));
                paramMap.add("file", resource);
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer" + "test");
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(
                        paramMap, headers);
                Map<String, String> pathValueMap = new HashMap<String, String>();
                pathValueMap.put("tenantId", rulaiTenantId);
                template.exchange("http://127.0.0.1:8989/v1/tenants/{tenantId}/qa/dialog",
                        HttpMethod.POST, entity, String.class, pathValueMap);
            }
        }

    }

  
    
    
    //    public static void main(String[] args) throws ParseException {
    //        if (args.length != 3) {
    //            System.err.println(
    //                    "Usage:java -jar client.jar easemob_tenant_id rulai_tenant_id option(0,1,2)");
    //            System.exit(1);
    //        }
    //
    //        String easemob_tenant_id = args[0];
    //        String rulai_tenant_id = args[1];
    //        int option = Integer.parseInt(args[2]);
    //
    //        RestTemplate template = new RestTemplate();
    //        Event param = new Event();
    //        param.setAccessToken("asdfasd");
    //        param.setOption(option);
    //        param.setRulaiTenantId(rulai_tenant_id);
    //        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //        param.setTimeBeginDate(dateFormat.parse("2015-12-01"));
    //        param.setTimeEndDate(dateFormat.parse("2016-04-01"));
    //
    //        String url = "http://127.0.0.1:9595/v1/tenants/" + easemob_tenant_id + "/easemob/dialog/";
    //
    //        String response = template.postForObject(url, param, String.class);
    //        System.out.println(response);

    //    }

    //    public static void main(String[] args) {
    //        Test service = new Test();
    //        service.upload("/Users/arthur/Workplace/data/dialog", "easemob_test_tenant");

    //        ExecutorService threadPool = Executors.newFixedThreadPool(10);
    //        List<Future<Long>> resultList = new ArrayList<Future<Long>>();
    //        for (int i = 0; i < 10; i++) {
    //            Future<Long> result = threadPool.submit(new TestWorker(i));
    //            resultList.add(result);
    //        }
    //
    //        for (int i = 0; i < resultList.size(); i++) {
    //            try {
    //                System.out.println(i + " " + resultList.get(i).get());
    //            } catch (Exception e) {}
    //        }

    //        String content;
    //        try {
    //            content = FileUtils.readLines(new File("lib/test")).get(0);
    //            JSONObject object = new JSONObject(content);
    //            String refData = object.getString("ref_data");
    //            System.out.println(refData);
    //            JSONObject refObject = new JSONObject(refData);
    //            JSONArray array = refObject.getJSONArray("rec_list");
    //            System.out.println(array.get(0).toString());
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //
    //        System.exit(0);

    //    }

}

class TestWorker implements Callable<Long> {

    int index;

    public TestWorker(int index) {
        this.index = index;
    }

    @Override
    public Long call() throws Exception {
        try {
            if (1 == index) {
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
}
