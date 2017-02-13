package com.kwery.tests.controllers.apis.integration.jobapicontroller.deletejobexecution;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kwery.models.JobExecutionModel.TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.kwery.views.ActionResult.Status.success;

public class JobApiControllerDeleteJobExecutionTest extends AbstractPostLoginApiTest {
    public List<Integer> ids = new ArrayList<>(2);
    protected JobExecutionModel last;

    @Before
    public void setUp() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        for (int i = 0; i < 2; ++i) {
            last = jobExecutionModel();
            last.setJobModel(jobModel);
            jobExecutionDbSetUp(last);
            ids.add(last.getId());
        }
    }

    @Test
    public void test() throws Exception {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobApiController.class, "deleteJobExecution", ImmutableMap.of("jobExecutionId", ids.get(0)));
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());
        assertJsonActionResult(response, new ActionResult(success, ""));
        new DbTableAsserter.DbTableAsserterBuilder(TABLE, jobExecutionTable(last)).build().assertTable();
    }
}
