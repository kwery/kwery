package com.kwery.tests.controllers.apis.integration.joblabelapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.JobLabelApiController;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobLabelApiControllerGetByIdTest extends AbstractPostLoginApiTest {
    private JobLabelModel model;
    private JobLabelModel parentModel;

    @Before
    public void setUp() {
        parentModel = TestUtil.jobLabelModel();
        DbUtil.jobLabelDbSetUp(parentModel);

        model = jobLabelModel();
        model.setParentLabel(parentModel);
        jobLabelDbSetUp(model);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobLabelApiController.class, "getJobLabelById",
                ImmutableMap.of("jobLabelId", model.getId()));
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());

        assertThat(response, hasJsonPath("$.jobLabelModel.id", is(model.getId())));
        assertThat(response, hasJsonPath("$.jobLabelModel.label", is(model.getLabel())));
        assertThat(response, hasJsonPath("$.parentJobLabelModel.label", is(parentModel.getLabel())));
        assertThat(response, hasJsonPath("$.parentJobLabelModel.id", is(parentModel.getId())));
    }
}
