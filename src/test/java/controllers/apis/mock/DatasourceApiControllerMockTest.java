package controllers.apis.mock;

import controllers.apis.DatasourceApiController;
import dao.DatasourceDao;
import models.Datasource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static controllers.MessageKeys.DATASOURCE_ADDITION_FAILURE;
import static controllers.MessageKeys.DATASOURCE_ADDITION_SUCCESS;
import static controllers.util.TestUtil.datasource;
import static models.Datasource.Type.MYSQL;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceApiControllerMockTest extends ControllerMockTest {
    @Mock
    private DatasourceDao dao;
    private DatasourceApiController controller;

    @Before
    public void before() {
        controller = new DatasourceApiController();
        controller.setMessages(messages);
        controller.setDatasourceDao(dao);
    }

    @Test
    public void testSuccess() {
        Datasource datasource = datasource();
        doNothing().when(dao).save(datasource);
        mockMessages(DATASOURCE_ADDITION_SUCCESS, MYSQL.name(), datasource.getLabel());
        assertSuccess(actionResult(controller.addDatasource(datasource, context)));
    }

    @Test
    public void testFailure() {
        Datasource datasource = datasource();
        when(dao.getByLabel(datasource.getLabel())).thenReturn(datasource);
        mockMessages(DATASOURCE_ADDITION_FAILURE, MYSQL.name(), datasource.getLabel());
        assertFailure(actionResult(controller.addDatasource(datasource, context)));
    }
}
