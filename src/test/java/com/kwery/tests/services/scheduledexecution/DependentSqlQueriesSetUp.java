package com.kwery.tests.services.scheduledexecution;

import com.kwery.models.SqlQueryModel;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;

import static com.kwery.models.SqlQueryModel.CRON_EXPRESSION_COLUMN;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class DependentSqlQueriesSetUp extends SchedulerServiceScheduledExecutionBaseTest {
    protected static int dependentSelectQueryId = 4;

    public void setUp() {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(dependentSelectQueryId, "", "dependentQuery", "select User from mysql.user where User = 'root'", 1)
                                .build(),

                        insertInto(SqlQueryModel.TABLE_QUERY_RUN_DEPENDENT)
                                .columns(SqlQueryModel.COLUMN_QUERY_RUN_ID_FK, SqlQueryModel.COLUMN_DEPENDENT_QUERY_RUN_ID_FK)
                                .values(successQueryId, dependentSelectQueryId)
                                .values(sleepQueryId, dependentSelectQueryId)
                                .values(failQueryId, dependentSelectQueryId)
                                .build(),
                        insertInto(SqlQueryModel.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                                .columns(SqlQueryModel.COLUMN_QUERY_RUN_ID_FK, SqlQueryModel.COLUMN_EMAIL)
                                .values(dependentSelectQueryId, recipientEmail)
                                .build()
                )
        ).launch();
    }
}
