package com.kwery.tests.services.oneoffexecution;

import com.kwery.models.SqlQuery;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;

import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class DependentSqlQueriesSetUp extends SchedulerServiceOneOffExecutionBaseTest {
    protected static int dependentSelectQueryId = 4;

    public void setUp() {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(dependentSelectQueryId, "", "dependentQuery", "select User from mysql.user where User = 'root'", 1)
                                .build(),

                        insertInto(SqlQuery.TABLE_QUERY_RUN_DEPENDENT)
                                .columns(SqlQuery.COLUMN_QUERY_RUN_ID_FK, SqlQuery.COLUMN_DEPENDENT_QUERY_RUN_ID_FK)
                                .values(successQueryId, dependentSelectQueryId)
                                .values(sleepQueryId, dependentSelectQueryId)
                                .values(failQueryId, dependentSelectQueryId)
                                .build(),
                        insertInto(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                                .columns(SqlQuery.COLUMN_QUERY_RUN_ID_FK, SqlQuery.COLUMN_EMAIL)
                                .values(dependentSelectQueryId, recipientEmail)
                                .build()
                )
        ).launch();
    }
}
