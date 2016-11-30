package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.MysqlDockerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import static com.kwery.conf.Routes.ADD_SQL_QUERY_API;
import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.SqlQueryModel.CRON_EXPRESSION_COLUMN;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static com.kwery.tests.util.Messages.QUERY_RUN_WITHOUT_CRON_ADDITION_SUCCESS_M;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerAddSqlQueryWithDependsOnSuccessTest extends AbstractPostLoginApiTest {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected SchedulerService schedulerService;

    protected int sqlQueryId = 1;
    protected int datasourceId = 1;

    protected Datasource datasource;

    @Before
    public void setUpSqlQueryApiControllerAddSqlQueryWithDependsOnSuccessTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasourceId, datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType().name(),
                                        datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(sqlQueryId, "* * * * *", "query", "select * from mysql.db", datasourceId)
                                .build()
                )
        ).launch();

        //So that the above query gets scheduled
        getInjector().getInstance(SchedulerService.class).scheduleAllQueries();

        sqlQueryExecutionDao = getInjector().getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();

        SqlQueryDto dto = new SqlQueryDto();
        dto.setLabel("select");
        dto.setQuery("select * from mysql.db");
        dto.setDependsOnSqlQueryId(sqlQueryId);
        dto.setDatasourceId(datasourceId);
        dto.setLabel("dependentQuery");

        assertSuccess(
                actionResult(ninjaTestBrowser.postJson(getUrl(ADD_SQL_QUERY_API), dto)),
                QUERY_RUN_WITHOUT_CRON_ADDITION_SUCCESS_M
        );

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryId);

        SqlQueryExecutionSearchFilter dependentQueryFilter = new SqlQueryExecutionSearchFilter();
        dependentQueryFilter.setSqlQueryId(100);

        await().atMost(150, SECONDS).until(() ->
                sqlQueryExecutionDao.filter(filter).size() > 0
                        && sqlQueryExecutionDao.filter(filter).get(0).getStatus() == SqlQueryExecution.Status.SUCCESS
                        && sqlQueryExecutionDao.filter(dependentQueryFilter).size() > 0
                        && sqlQueryExecutionDao.filter(dependentQueryFilter).get(0).getStatus() == SqlQueryExecution.Status.SUCCESS
        );

        List<SqlQueryExecution> executions = sqlQueryExecutionDao.filter(filter);

        SqlQueryExecution sqlQueryExecution = executions.get(0);

        assertThat(sqlQueryExecution.getExecutionStart(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionEnd(), greaterThan(start));
        assertThat(sqlQueryExecution.getExecutionId(), notNullValue());
        assertThat(nullToEmpty(sqlQueryExecution.getResult()), not(equalTo("")));
        assertThat(sqlQueryExecution.getStatus(), is(SUCCESS));

        List<SqlQueryExecution> dependentQueryExecutions = sqlQueryExecutionDao.filter(dependentQueryFilter);

        SqlQueryExecution execution = dependentQueryExecutions.get(0);

        assertThat(execution.getStatus(), is(SUCCESS));
        assertThat(execution.getExecutionStart(), greaterThan(start));
        assertThat(execution.getExecutionEnd(), greaterThan(execution.getExecutionStart()));
        assertThat(execution.getExecutionId(), notNullValue());
        assertThat(execution.getResult(), notNullValue());
    }
}
