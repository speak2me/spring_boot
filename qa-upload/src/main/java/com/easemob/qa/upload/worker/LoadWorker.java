package com.easemob.qa.upload.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.qa.upload.persistence.entity.Chatmessage;
import com.easemob.qa.upload.persistence.entity.ChatmessageCriteria;
import com.easemob.qa.upload.persistence.mapper.ChatmessageMapper;
import com.easemob.qa.upload.service.IConst;
import com.easemob.qa.upload.service.PoolService;

public class LoadWorker implements Callable<Boolean> {

    String dataPath;

    int easemobTenantId;

    String rulaiTenantId;

    int index;

    List<String> dataList;

    ChatmessageMapper operator;

    PoolService poolService;

    public LoadWorker(int index, String path, String rulaiTenantId, int easemobTenantId,
            final List<String> sessionList, final ChatmessageMapper operator,
            final PoolService threadPool) {
        this.index = index;
        this.dataPath = path;
        this.rulaiTenantId = rulaiTenantId;
        int to_index = (index + 1) * IConst.CHAT_PAGE_SIZE;
        if (to_index > sessionList.size()) {
            to_index = sessionList.size();
        }
        this.dataList = sessionList.subList(index * IConst.CHAT_PAGE_SIZE, to_index);
        this.operator = operator;
        this.poolService = threadPool;
        this.easemobTenantId = easemobTenantId;
    }

    private String getSimple(String complex) {
        String body = "";
        try {
            JSONObject object = new JSONObject(complex);
            JSONArray array = object.optJSONArray("bodies");
            String type = array.getJSONObject(0).optString("type");
            if (type.equalsIgnoreCase("txt")) {
                body = array.getJSONObject(0).optString("msg");
            }
        } catch (JSONException e) {
            String content = complex.replaceFirst("\"type\":\"[a-zA-Z]+\",", "");
            JSONObject object = new JSONObject(content);
            JSONArray array = object.optJSONArray("bodies");
            String type = array.getJSONObject(0).optString("type");
            if (type.equalsIgnoreCase("txt")) {
                body = array.getJSONObject(0).optString("msg");
            }
        }
        return body;
    }

    @Override
    public Boolean call() throws Exception {
        ArrayList<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>();
        boolean flag = true;
        StringBuffer fileBuffer = new StringBuffer();
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject object = new JSONObject();
            String sessionId = dataList.get(i);
            try {
                object.put("tenant_id", rulaiTenantId);
                object.put("session_id", sessionId);
                object.put("type", "live_chat");
                object.put("timestamp", System.currentTimeMillis());//
            } catch (JSONException e) {}

            ChatmessageCriteria criteria = new ChatmessageCriteria();
            criteria.createCriteria().andSessionserviceidEqualTo(sessionId)
                    .andTenantidEqualTo(easemobTenantId);
            criteria.setOrderByClause("createDateTime asc");
            List<Chatmessage> chatList = operator.selectByExample(criteria);

            JSONArray array = new JSONArray();
            for (Chatmessage message : chatList) {
                try {
                    JSONObject one = new JSONObject();
                    switch (message.getFromuserUsertype()) {
                        case "Scheduler":
                            continue;
                        case "Visitor":
                            String question = getSimple(message.getBody());
                            if (!question.equals("")) {
                                one.put("Q", question);
                            } else {
                                continue;
                            }
                            break;
                        case "Agent":
                            String answer = getSimple(message.getBody());
                            if (!answer.equals("")) {
                                one.put("A", answer);
                            } else {
                                continue;
                            }
                            break;
                        case "Robot":
                            continue;
                        default:
                            break;
                    }
                    //one.append("time", message.getCreatedatetime().getTime());
                    array.put(one);
                } catch (JSONException e) {}
            }
            try {
                object.put("session", array);
            } catch (JSONException e) {}

            long currentSize = fileBuffer.toString().length();
            long sessionSize = object.toString().length();
            if ((currentSize + sessionSize) > IConst.FILE_MAX_LENGTH) {
                FileWorker worker = new FileWorker(dataPath, rulaiTenantId, index,
                        fileBuffer.toString());
                Future<Boolean> reuslt = poolService.compress(worker);
                resultList.add(reuslt);
                fileBuffer = null;
                fileBuffer = new StringBuffer();
            } else {
                fileBuffer.append(object.toString()).append("\n");
            }
        }
        if (fileBuffer.toString().length() != 0) {
            FileWorker worker = new FileWorker(dataPath, rulaiTenantId, index,
                    fileBuffer.toString());
            Future<Boolean> reuslt = poolService.compress(worker);
            resultList.add(reuslt);
            fileBuffer = null;
        }
        for (int i = 0; i < resultList.size(); i++) {
            flag &= resultList.get(i).get();
            if (flag == false) {
                break;
            }
        }
        return flag;
    }

}
