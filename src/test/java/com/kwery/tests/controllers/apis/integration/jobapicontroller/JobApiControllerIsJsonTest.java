package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobApiController;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerIsJsonTest {
    private JobApiController jobApiController = new JobApiController(null, null, null, null, null, null, null, null, null, null, null);

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
