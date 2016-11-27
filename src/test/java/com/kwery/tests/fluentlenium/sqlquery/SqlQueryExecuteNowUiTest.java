package com.kwery.tests.fluentlenium.sqlquery;

import com.google.common.collect.ImmutableMap;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;
import java.util.Map;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryExecuteNowUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected SqlQueryListPage page;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected int userQueryId = 1;
    protected int dbQueryId = 2;

    protected String userQueryLabel = "userQuery";
    protected String dbQueryLabel = "dbQuery";

    protected Map<String, Integer> labelQueryIdMap = ImmutableMap.of(
        userQueryLabel, userQueryId,
        dbQueryLabel, dbQueryId
    );

    protected Map<String, String> labelResultMap = ImmutableMap.of(
            userQueryLabel, "[[\"User\"],[\"root\"]]",
            dbQueryLabel, "[[\"Db\"],[\"sys\"]]"
    );

    @Before
    public void setUpExecuteNowSqlQueryPageTest() {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(userQueryId, "", userQueryLabel, "select User from mysql.user where User = 'root'", 1)
                                .values(dbQueryId, "", dbQueryLabel, "select Db from mysql.db", 1)
                                .build()
                )
        ).launch();

        page = createPage(SqlQueryListPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }

        sqlQueryExecutionDao = ninjaServerRule.getInjector().getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws InterruptedException {
        page.waitForRows(2);
        executeQuery(userQueryLabel);
        executeQuery(dbQueryLabel);
    }

    private void executeQuery(String label) throws InterruptedException {
        List<List<String>> rows = page.rows();

        for (int i = 0 ;i < 2; ++i) {
            List<String> row = rows.get(i);

            if (label.equals(row.get(0))) {
                page.executeNow(i);
                break;
            }
        }

        page.waitForExecuteNowSuccessMessage(label);

        Awaitility.waitAtMost(30, SECONDS).until(() -> !getExecutions(label).isEmpty());

        SqlQueryExecution sqlQueryExecution = getExecutions(label).get(0);

        assertThat(sqlQueryExecution.getResult(), is(labelResultMap.get(label)));
    }

    private List<SqlQueryExecution> getExecutions(String label) {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(labelQueryIdMap.get(label));
        List<SqlQueryExecution> executions = sqlQueryExecutionDao.filter(filter);
        return executions;
    }
}
