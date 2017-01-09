package com.kwery.tests.controllers.apis.integration.joblabelapicontroller;

import com.kwery.controllers.apis.JobLabelApiController;
import com.kwery.dao.JobLabelDao;
import com.kwery.dtos.JobLabelDto;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobLabelApiControllerSaveJobLabelTest extends AbstractPostLoginApiTest {
    private JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobLabelApiController.class, "saveJobLabel");
        JobLabelDto dto = new JobLabelDto();
        dto.setLabelName("foobarmoo");
        dto.setParentLabelId(jobLabelModel.getId());

        String response = ninjaTestBrowser.postJson(getUrl(url), dto);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));

        JobLabelDao jobLabelDao = getInjector().getInstance(JobLabelDao.class);
        JobLabelModel fromDb = jobLabelDao.getJobLabelModelByLabel("foobarmoo");

        assertThat(fromDb.getParentLabel().getId(), is(jobLabelModel.getId()));
        assertThat(fromDb.getLabel(), is("foobarmoo"));

        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
    }
}
