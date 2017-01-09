package com.kwery.tests.controllers.apis.integration.joblabelapicontroller;

import com.google.common.collect.ImmutableList;
import com.jayway.jsonpath.matchers.JsonPathMatchers;
import com.kwery.controllers.apis.JobLabelApiController;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public class JobLabelApiControllerGetAllTest extends AbstractPostLoginApiTest {

    private JobLabelModel parentJobLabelModel;
    private JobLabelModel childJobLabelModel;

    @Before
    public void setUp() {
        parentJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel);

        childJobLabelModel = jobLabelModel();
        childJobLabelModel.setParentLabel(parentJobLabelModel);
        jobLabelDbSetUp(childJobLabelModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobLabelApiController.class, "getAllJobLabels");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, JsonPathMatchers.isJson());
        assertThat(response, JsonPathMatchers.hasJsonPath("$.[*]", hasSize(2)));

        for (JobLabelModel jobLabelModel : ImmutableList.of(parentJobLabelModel, childJobLabelModel)) {
            assertThat(response, JsonPathMatchers.hasJsonPath("$.[*].jobLabelModel.label", hasItem(jobLabelModel.getLabel())));
        }

        assertThat(response, JsonPathMatchers.hasJsonPath("$.[*].parentJobLabelModel.label", hasItem(parentJobLabelModel.getLabel())));
        assertThat(response, JsonPathMatchers.hasJsonPath("$.[*].jobLabelModel.childLabels[*].label", hasItem(childJobLabelModel.getLabel())));
    }
}
