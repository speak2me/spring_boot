package com.easemob.qa.upload;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.easemob.qa.upload.model.Event;
import com.easemob.qa.upload.utils.JSONUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestQAApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/application-test.properties")
public class UploadControllerTest extends AbstractControllerTest {

    @Test
    public void testUpload0() {
        int easemobTenantId = 1467;
        Event one = new Event();
        one.setAccessToken("");
        one.setOption(0);
        one.setRulaiTenantId("easemob_1467");
        String beginTime = "2015-09-01";
        String endTime = "2016-04-30";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            one.setTimeBeginDate(format.parse(beginTime));
            one.setTimeEndDate(format.parse(endTime));
        } catch (ParseException e) {}
        String body = JSONUtil.mapToJsonString(one);
        try {
            String resultStr = mockMvc
                    .perform(post(UPLOAD_URI, easemobTenantId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
                    .andExpect(status().isOk()).andDo(print()).andReturn().getResponse()
                    .getContentAsString();
            System.out.println(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUpload1() {
        int easemobTenantId = 1467;
        Event one = new Event();
        one.setAccessToken("");
        one.setOption(1);
        one.setRulaiTenantId("easemob_1467");
        String beginTime = "2015-09-01";
        String endTime = "2015-09-30";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            one.setTimeBeginDate(format.parse(beginTime));
            one.setTimeEndDate(format.parse(endTime));
        } catch (ParseException e) {}
        String body = JSONUtil.mapToJsonString(one);
        try {
            String resultStr = mockMvc
                    .perform(post(UPLOAD_URI, easemobTenantId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
                    .andExpect(status().isOk()).andDo(print()).andReturn().getResponse()
                    .getContentAsString();
            System.out.println(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUpload2() {
        int easemobTenantId = 1467;
        Event one = new Event();
        one.setAccessToken("");
        one.setOption(2);
        one.setRulaiTenantId("easemob_1467");
        String beginTime = "2015-09-01";
        String endTime = "2015-09-30";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            one.setTimeBeginDate(format.parse(beginTime));
            one.setTimeEndDate(format.parse(endTime));
        } catch (ParseException e) {}
        String body = JSONUtil.mapToJsonString(one);
        try {
            String resultStr = mockMvc
                    .perform(post(UPLOAD_URI, easemobTenantId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
                    .andExpect(status().isOk()).andDo(print()).andReturn().getResponse()
                    .getContentAsString();
            System.out.println(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCheckStatus() {
        String rulaiTenantId = "easemob_1410";
        try {
            String resultStr = mockMvc
                    .perform(get(CHECK_URI, rulaiTenantId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk()).andDo(print()).andReturn().getResponse()
                    .getContentAsString();
            System.out.println(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
