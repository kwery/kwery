package controllers.mock;

import com.google.common.base.Optional;
import controllers.DatasourceController;
import dao.DatasourceDao;
import models.Datasource;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import views.ActionResult;

import static controllers.MessageKeys.DATA_SOURCE_ADDITION_FAILURE;
import static controllers.MessageKeys.DATA_SOURCE_ADDITION_SUCCESS;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceControllerMockTest {
    @Mock
    private DatasourceDao dao;
    @Mock
    private Messages messages;
    @Mock
    private Context context;
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
        when(messages.get(eq(DATA_SOURCE_ADDITION_SUCCESS), eq(context), any(Optional.class), eq(MYSQL), eq("label"))).thenReturn(Optional.of(message));

        Result additionResult = controller.addDatasource(datasource, context);

        ActionResult actionResult = (ActionResult) additionResult.getRenderable();

        assertEquals("Datasource creation result message matches", message, actionResult.getMessage());
        assertEquals("Datasource creation status is success", success, actionResult.getStatus());
    }

    @Test
    public void testFailure() {
        Datasource datasource = datasource();
        when(dao.getByLabel(datasource.getLabel())).thenReturn(datasource);
        String msg = "foo";
        when(messages.get(eq(DATA_SOURCE_ADDITION_FAILURE), eq(context), any(Optional.class), eq(MYSQL), eq(datasource.getLabel()))).thenReturn(Optional.of(msg));

        Result additionResult = controller.addDatasource(datasource, context);

        ActionResult actionResult = (ActionResult) additionResult.getRenderable();

        assertThat(actionResult.getMessage(), is(msg));
        assertThat(actionResult.getStatus(), is(failure));
    }

    private Datasource datasource() {
        Datasource datasource = new Datasource();
        datasource.setUrl("url");
        datasource.setUsername("username");
        datasource.setPassword("password");
        datasource.setLabel("label");
        datasource.setType(MYSQL);
        return datasource;
    }
}
