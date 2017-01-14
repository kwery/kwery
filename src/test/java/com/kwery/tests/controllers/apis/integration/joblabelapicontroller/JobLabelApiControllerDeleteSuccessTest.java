package com.kwery.tests.controllers.apis.integration.joblabelapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobLabelApiController;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.HashMap;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.views.ActionResult.Status.success;

public class JobLabelApiControllerDeleteSuccessTest extends AbstractPostLoginApiTest {
    private JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobLabelApiController.class, "deleteJobLabelById", ImmutableMap.of("jobLabelId", jobLabelModel.getId())
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        ActionResult expected = new ActionResult(success, MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_SUCCESS_M, jobLabelModel.getLabel()));
        assertJsonActionResult(response, expected);
    }
}
