package com.easemob.qa.upload;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

public class AbstractControllerTest {

    protected static final String CHECK_URI = "/v1/tenants/status/{rulaiTenantId}";

    protected static final String UPLOAD_URI = "/v1/tenants/{tenantId}/easemob/dialog/";
    

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @After
    public void tearDown() {
    }

}
