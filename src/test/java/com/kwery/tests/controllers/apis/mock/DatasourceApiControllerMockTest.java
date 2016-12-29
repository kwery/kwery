package com.kwery.tests.controllers.apis.mock;

import com.kwery.models.Datasource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.kwery.controllers.MessageKeys.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.util.TestUtil.datasource;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceApiControllerMockTest extends AbstractDatasourceApiControllerMockTest {
    @Test
    public void testAddSuccess() {
        Datasource datasource = datasource();
        doNothing().when(datasourceDao).save(datasource);
        when(datasourceService.testConnection(datasource)).thenReturn(true);
        mockMessages(DATASOURCE_ADDITION_SUCCESS, MYSQL.name(), datasource.getLabel());
        assertSuccess(actionResult(datasourceApiController.addDatasource(datasource, context, validation)));
    }

    @Test
    public void testAddFailure() {
        Datasource datasource = datasource();

        when(datasourceDao.getByLabel(datasource.getLabel())).thenReturn(datasource);
        String alreadyExistsErrorMessage = "foo";
        mockMessagesWithReturn(DATASOURCE_ADDITION_FAILURE, alreadyExistsErrorMessage, MYSQL.name(), datasource.getLabel());

        when(datasourceService.testConnection(datasource)).thenReturn(false);
        String connectionFailedErrorMessage = "bar";
        mockMessagesWithReturn(DATASOURCE_CONNECTION_FAILURE, connectionFailedErrorMessage);

        assertFailure(
                actionResult(datasourceApiController.addDatasource(datasource, context, validation)),
                alreadyExistsErrorMessage, connectionFailedErrorMessage
        );
    }

    @Test
    public void testConnectionTestSuccess() {
        Datasource datasource = datasource();
        when(datasourceService.testConnection(datasource)).thenReturn(true);
        mockMessages(DATASOURCE_CONNECTION_SUCCESS);
        assertSuccess(actionResult(datasourceApiController.testConnection(datasource, context)));
    }

    @Test
    public void testConnectionTestFailure() {
        Datasource datasource = datasource();
        when(datasourceService.testConnection(datasource)).thenReturn(false);
        mockMessages(DATASOURCE_CONNECTION_FAILURE);
        assertFailure(actionResult(datasourceApiController.testConnection(datasource, context)));
    }
}
