package controllers.mock;

import controllers.DatasourceController;
import dao.DatasourceDao;
import models.Datasource;
import ninja.Context;
import ninja.i18n.Messages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import views.ActionResult;

import static controllers.MessageKeys.DATASOURCE_ADDITION_FAILURE;
import static controllers.MessageKeys.DATASOURCE_ADDITION_SUCCESS;
import static controllers.util.TestUtil.datasource;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceControllerMockTest extends AbstractControllerMockTest {
    @Mock
    private DatasourceDao dao;
    private DatasourceController controller;

    @Before
    public void before() {
        controller = new DatasourceController();
        controller.setMessages(messages);
        controller.setDatasourceDao(dao);
    }

    @Test
    public void testSuccess() {
        Datasource datasource = datasource();

        doNothing().when(dao).save(datasource);

        String message = "success";
        mockMessages(DATASOURCE_ADDITION_SUCCESS, message, MYSQL.name(), datasource.getLabel());

        ActionResult actionResult = actionResult(controller.addDatasource(datasource, context));

        assertEquals("Datasource creation result message matches", message, actionResult.getMessage());
        assertEquals("Datasource creation status is success", success, actionResult.getStatus());
    }

    @Test
    public void testFailure() {
        Datasource datasource = datasource();
        when(dao.getByLabel(datasource.getLabel())).thenReturn(datasource);
        String msg = "foo";
        mockMessages(DATASOURCE_ADDITION_FAILURE, msg, MYSQL.name(), datasource.getLabel());

        ActionResult actionResult = actionResult(controller.addDatasource(datasource, context));

        assertThat(actionResult.getMessage(), is(msg));
        assertThat(actionResult.getStatus(), is(failure));
    }

}
