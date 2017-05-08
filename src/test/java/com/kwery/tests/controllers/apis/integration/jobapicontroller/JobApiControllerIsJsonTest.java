package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerIsJsonTest extends RepoDashTestBase {
    private JobApiController jobApiController;

    @Before
    public void setUp() {
        jobApiController = getInstance(JobApiController.class);
    }

    @Test
    public void testNotJson() {
        String notJson = "foo";
        assertThat(jobApiController.isJson(notJson), is(false));
    }

    @Test
    public void testJson() {
        String notJson = "{\"key\": \"value\"}";
        assertThat(jobApiController.isJson(notJson), is(true));
    }
}
