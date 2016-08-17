package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DatasourceDao;
import models.Datasource;
import org.junit.Before;
import org.junit.Test;
import views.ActionResult;

import java.io.IOException;
import java.text.MessageFormat;

import static conf.Routes.ONBOARDING_ADD_DATASOURCE;
import static controllers.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static controllers.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

public class DatasourceControllerTest extends DashRepoNinjaTest {
    private DatasourceDao dao;

    @Before
    public void before() {
        dao = getInjector().getInstance(DatasourceDao.class);
    }

    @Test
    public void test() throws IOException {
        Datasource datasource = new Datasource();
        datasource.setUsername("purvi");
        datasource.setPassword("password");
        datasource.setUrl("foo.com");
        datasource.setLabel("test");
        datasource.setType(MYSQL);

        String url = getUrl(ONBOARDING_ADD_DATASOURCE);

        String successResponse = ninjaTestBrowser.postJson(url, datasource);

        ActionResult successResult = new ObjectMapper().readValue(successResponse, ActionResult.class);

        assertTrue("Datasource added successfully", successResult.getStatus() == success);

        String successMessage = MessageFormat.format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, "test");
        assertEquals("Datasource addition success message matches", successMessage, successResult.getMessage());

        String failureResponse = ninjaTestBrowser.postJson(url, datasource);
        ActionResult failureResult = new ObjectMapper().readValue(failureResponse, ActionResult.class);
        assertThat(failureResult.getStatus(), is(failure));

        String failureMessage = MessageFormat.format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, "test");
        assertThat(failureResult.getMessage(), is(failureMessage));

        assertThat(dao.getByLabel("test"), notNullValue());
    }
}
