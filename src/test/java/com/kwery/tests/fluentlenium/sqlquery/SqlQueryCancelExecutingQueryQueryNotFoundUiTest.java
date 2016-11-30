package com.kwery.tests.fluentlenium.sqlquery;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.util.*;
import org.awaitility.Awaitility;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Collections;
import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.Status.ONGOING;
import static com.kwery.tests.util.Messages.KILL_QUERY_NOT_FOUND_M;
import static com.kwery.tests.util.TestUtil.sleepSqlQuery;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryCancelExecutingQueryQueryNotFoundUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected SchedulerService schedulerService;
    protected SqlQueryExecutingListPage page;
    protected SqlQueryModel sqlQuery;

    @Before
    public void setUpListExecutingSqlQueriesCancelSuccessTest () throws InterruptedException, SqlQueryExecutionNotFoundException {
        Datasource datasource = mysqlDockerRule.getMySqlDocker().datasource();

        ninjaServerRule.getInjector().getInstance(DatasourceDao.class).save(datasource);

        this.sqlQuery = sleepSqlQuery(datasource);
        ninjaServerRule.getInjector().getInstance(SqlQueryDao.class).save(sqlQuery);

        schedulerService = ninjaServerRule.getInjector().getInstance(SchedulerService.class);
        schedulerService.schedule(sqlQuery);

        SqlQueryExecutionDao sqlQueryExecutionDao = ninjaServerRule.getInjector().getInstance(SqlQueryExecutionDao.class);

        Awaitility.await().atMost(60, SECONDS).until(() -> !getSqlQueryExecutions(sqlQueryExecutionDao).isEmpty());

        page = createPage(SqlQueryExecutingListPage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Sql Query executing queries page not rendered");
        }

        List<SqlQueryExecutionModel> models = getSqlQueryExecutions(sqlQueryExecutionDao);

        Collections.sort(models, (o1, o2) -> o1.getExecutionStart().compareTo(o2.getExecutionStart()));

        schedulerService.stopExecution(sqlQuery.getId(), models.get(0).getExecutionId());
    }

    private List<SqlQueryExecutionModel> getSqlQueryExecutions(SqlQueryExecutionDao sqlQueryExecutionDao) {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(ONGOING));
        return sqlQueryExecutionDao.filter(filter);
    }

    @Test
    public void test() throws InterruptedException {
        FluentWebElement killButton = $("#executingSqlQueriesTable tr td button", 0);
        assertThat(killButton.getText().toLowerCase(), is(Messages.KILL_M.toLowerCase()));
        killButton.click();
        await().atMost(30, SECONDS).until(() -> KILL_QUERY_NOT_FOUND_M.equals($("#executingSqlQueriesTable tr td p", 0).getText()));
    }
}
