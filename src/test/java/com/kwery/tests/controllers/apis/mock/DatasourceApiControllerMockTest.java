package com.kwery.tests.controllers.apis.mock;

import com.kwery.models.Datasource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.controllers.MessageKeys.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.util.Messages.DATASOURCEAPICONTROLLER_CONNECTION_ERROR_ERROR_CODE_M;
import static com.kwery.tests.util.Messages.DATASOURCEAPICONTROLLER_CONNECTION_ERROR_SQL_STATE_M;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.datasourceWithoutId;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DatasourceApiControllerMockTest extends AbstractDatasourceApiControllerMockTest {
    @Test
    public void testAddSuccess() throws SQLException {
        Datasource datasource = datasourceWithoutId();
        doNothing().when(datasourceDao).save(datasource);
        doNothing().when(datasourceService).connect(datasource);
        mockMessages(DATASOURCE_ADDITION_SUCCESS, MYSQL.name(), datasource.getLabel());
        assertSuccess(actionResult(datasourceApiController.addDatasource(datasource, context, validation)));
    }

    @Test
    public void testAddFailure() throws SQLException {
        Datasource datasource = datasourceWithoutId();

        when(datasourceDao.getByLabel(datasource.getLabel())).thenReturn(datasource);
        String alreadyExistsErrorMessage = "foo";
        mockMessagesWithReturn(DATASOURCE_ADDITION_FAILURE, alreadyExistsErrorMessage, MYSQL.name(), datasource.getLabel());

        SQLException sqlException = mock(SQLException.class);
        when(sqlException.getLocalizedMessage()).thenReturn("localized message.");
        when(sqlException.getSQLState()).thenReturn("sql state");
        when(sqlException.getErrorCode()).thenReturn(10);

        doThrow(sqlException).when(datasourceService).connect(datasource);

        String connectionFailedErrorMessage = "bar";
        mockMessagesWithReturn(DATASOURCE_CONNECTION_FAILURE, connectionFailedErrorMessage, MYSQL.name());
        mockMessagesWithReturn(DATASOURCEAPICONTROLLER_CONNECTION_ERROR_SQL_STATE, DATASOURCEAPICONTROLLER_CONNECTION_ERROR_SQL_STATE_M);
        mockMessagesWithReturn(DATASOURCEAPICONTROLLER_CONNECTION_ERROR_ERROR_CODE, DATASOURCEAPICONTROLLER_CONNECTION_ERROR_ERROR_CODE_M);

        List<String> messageParts = new LinkedList<>();

        messageParts.add(connectionFailedErrorMessage);
        messageParts.add("localized message");
        messageParts.add(DATASOURCEAPICONTROLLER_CONNECTION_ERROR_SQL_STATE_M + " - " + "sql state");
        messageParts.add(DATASOURCEAPICONTROLLER_CONNECTION_ERROR_ERROR_CODE_M + " - " + "10.");

        assertFailure(
                actionResult(datasourceApiController.addDatasource(datasource, context, validation)),
                alreadyExistsErrorMessage, String.join(". ", messageParts)
        );
    }
}
