package com.kwery.tests.controllers.apis.integration.joblabelapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobLabelApiController;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.HashMap;

import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_HAS_CHILDREN_M;
import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_JOB_HAS_LABEL_M;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.views.ActionResult.Status.failure;

public class JobApiControllerDeleteFailureTest extends AbstractPostLoginApiTest {
    private JobLabelModel parentJobLabelModel;

    @Before
    public void setUp() {
        parentJobLabelModel = TestUtil.jobLabelModel();
        DbUtil.jobLabelDbSetUp(parentJobLabelModel);

        JobLabelModel jobLabelModel = TestUtil.jobLabelModel();
        jobLabelModel.setParentLabel(parentJobLabelModel);
        DbUtil.jobLabelDbSetUp(jobLabelModel);

        JobModel jobModel = TestUtil.jobModelWithoutDependents();
        DbUtil.jobDbSetUp(jobModel);

        jobModel.getLabels().add(parentJobLabelModel);

        DbUtil.jobJobLabelDbSetUp(jobModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                JobLabelApiController.class, "deleteJobLabelById", ImmutableMap.of("jobLabelId", parentJobLabelModel.getId())
        );
        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        ActionResult expected = new ActionResult(
                failure,
                ImmutableList.of(
                        MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_JOB_HAS_LABEL_M, parentJobLabelModel.getLabel()),
                        MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_HAS_CHILDREN_M, parentJobLabelModel.getLabel())
                )
                );
        assertJsonActionResult(response, expected);
    }
}
