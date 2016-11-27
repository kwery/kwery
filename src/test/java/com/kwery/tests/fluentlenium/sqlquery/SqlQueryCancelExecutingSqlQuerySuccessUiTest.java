package com.kwery.tests.fluentlenium.sqlquery;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.awaitility.Awaitility;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static com.kwery.models.SqlQueryExecution.Status.ONGOING;
import static com.kwery.tests.util.Messages.KILLED_M;
import static com.kwery.tests.util.Messages.KILL_M;
import static com.kwery.tests.util.TestUtil.sleepSqlQuery;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryCancelExecutingSqlQuerySuccessUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected SchedulerService schedulerService;
    protected SqlQueryExecutingListPage page;
    protected SqlQuery sqlQuery;

    @Before
    public void setUpListExecutingSqlQueriesCancelSuccessTest () throws InterruptedException, SqlQueryExecutionNotFoundException {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();

        ninjaServerRule.getInjector().getInstance(DatasourceDao.class).save(datasource);

        this.sqlQuery = sleepSqlQuery(datasource);
        ninjaServerRule.getInjector().getInstance(SqlQueryDao.class).save(sqlQuery);

        schedulerService = ninjaServerRule.getInjector().getInstance(SchedulerService.class);
        schedulerService.schedule(sqlQuery);

        SqlQueryExecutionDao sqlQueryExecutionDao = ninjaServerRule.getInjector().getInstance(SqlQueryExecutionDao.class);

        Awaitility.await().atMost(60, SECONDS).until(() -> {
            SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
            filter.setStatuses(ImmutableList.of(ONGOING));
            return !sqlQueryExecutionDao.filter(filter).isEmpty();
        });

        page = createPage(SqlQueryExecutingListPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Sql Query executing queries page not rendered");
        }
    }

    @Test
    public void test() throws InterruptedException {
        FluentWebElement killButton = $("#executingSqlQueriesTable tr td button", 0);
        assertThat(killButton.getText().toLowerCase(), is(KILL_M.toLowerCase()));
        killButton.click();
        await().atMost(30, SECONDS).until(() -> KILLED_M.equals(killButton.getText()));
    }
}
