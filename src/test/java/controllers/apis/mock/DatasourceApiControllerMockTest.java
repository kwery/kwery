package controllers.apis.mock;

import models.Datasource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static controllers.MessageKeys.DATASOURCE_ADDITION_FAILURE;
import static controllers.MessageKeys.DATASOURCE_ADDITION_SUCCESS;
import static controllers.MessageKeys.MYSQL_DATASOURCE_CONNECTION_FAILURE;
import static controllers.MessageKeys.MYSQL_DATASOURCE_CONNECTION_SUCCESS;
import static models.Datasource.Type.MYSQL;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static util.TestUtil.datasource;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceApiControllerMockTest extends AbstractDatasourceApiControllerMockTest {
    @Test
    public void testAddSuccess() {
        Datasource datasource = datasource();
        doNothing().when(datasourceDao).save(datasource);
        when(mysqlDatasourceService.testConnection(datasource)).thenReturn(true);
        mockMessages(DATASOURCE_ADDITION_SUCCESS, MYSQL.name(), datasource.getLabel());
        assertSuccess(actionResult(datasourceApiController.addDatasource(datasource, context, validation)));
    }

    @Test
    public void testAddFailure() {
        Datasource datasource = datasource();

        when(datasourceDao.getByLabel(datasource.getLabel())).thenReturn(datasource);
        String alreadyExistsErrorMessage = "foo";
        mockMessagesWithReturn(DATASOURCE_ADDITION_FAILURE, alreadyExistsErrorMessage, MYSQL.name(), datasource.getLabel());

        when(mysqlDatasourceService.testConnection(datasource)).thenReturn(false);
        String connectionFailedErrorMessage = "bar";
        mockMessagesWithReturn(MYSQL_DATASOURCE_CONNECTION_FAILURE, connectionFailedErrorMessage);

        assertFailure(
                actionResult(datasourceApiController.addDatasource(datasource, context, validation)),
                alreadyExistsErrorMessage, connectionFailedErrorMessage
        );
    }

    @Test
    public void testConnectionTestSuccess() {
        Datasource datasource = datasource();
        when(mysqlDatasourceService.testConnection(datasource)).thenReturn(true);
        mockMessages(MYSQL_DATASOURCE_CONNECTION_SUCCESS);
        assertSuccess(actionResult(datasourceApiController.testConnection(datasource, context)));
    }

    @Test
    public void testConnectionTestFailure() {
        Datasource datasource = datasource();
        when(mysqlDatasourceService.testConnection(datasource)).thenReturn(false);
        mockMessages(MYSQL_DATASOURCE_CONNECTION_FAILURE);
        assertFailure(actionResult(datasourceApiController.testConnection(datasource, context)));
    }
}
