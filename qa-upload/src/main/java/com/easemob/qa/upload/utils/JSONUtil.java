package com.easemob.qa.upload.utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    public static String mapToJsonString(Object contentsObj) {
        try {
            return mapper.writeValueAsString(contentsObj);
        } catch (JsonProcessingException e) {
            log.error("Failed to write {} to string", contentsObj, e);
        }
        return "";
    }

    public static List<LinkedHashMap<String, Object>> stringToMapList(String content) {
        try {
            @SuppressWarnings("unchecked")
            List<LinkedHashMap<String, Object>> orderedMapList = mapper.readValue(content,
                    List.class);
            return orderedMapList;
        } catch (IOException e) {
            log.error("fail to transfer {} to ListMap", content, e);
        }
        return null;
    }

}
