package com.easemob.qa.upload.server;

import com.google.common.html.HtmlEscapers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class CustomObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = 4770801906598607709L;

    public CustomObjectMapper() {
        SimpleModule module = new SimpleModule("HTML XSS Serializer",
                new Version(1, 0, 0, "FINAL", "com.easemob.qa.upload", "xssModule"));
        module.addSerializer(new JsonHtmlXssSerializer(String.class));
        this.registerModule(module);
    }

    public static class JsonHtmlXssSerializer extends JsonSerializer<String> {

        public JsonHtmlXssSerializer(Class<String> string) {
            super();
        }

        @Override
        public Class<String> handledType() {
            return String.class;
        }

        @Override
        public void serialize(String value, JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            if (value != null) {
                String encodedValue = HtmlEscapers.htmlEscaper().escape(value);
                jsonGenerator.writeString(encodedValue);
            }
        }
    }
}
