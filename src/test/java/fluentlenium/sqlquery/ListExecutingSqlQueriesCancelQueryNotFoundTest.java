package fluentlenium.sqlquery;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import dao.SqlQueryExecutionDao;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.UserTableUtil;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import models.User;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;
import services.scheduler.SqlQueryExecutionNotFoundException;
import util.Messages;
import util.MySqlDocker;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static fluentlenium.utils.DbUtil.getDatasource;
import static models.SqlQueryExecution.Status.ONGOING;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static util.TestUtil.sleepSqlQuery;

public class ListExecutingSqlQueriesCancelQueryNotFoundTest extends RepoDashFluentLeniumTest {
    protected MySqlDocker mySqlDocker = new MySqlDocker();
    protected SchedulerService schedulerService;
    protected ListExecutingSqlQueriesPage page;
    protected SqlQuery sqlQuery;

    @Before
    public void setUpListExecutingSqlQueriesCancelSuccessTest () throws InterruptedException, SqlQueryExecutionNotFoundException {
        mySqlDocker.start();

        UserTableUtil userTableUtil = new UserTableUtil();
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        userTableUtil.insertOperation()
                )
        );
        dbSetup.launch();

        Datasource datasource = mySqlDocker.datasource();
        getInjector().getInstance(DatasourceDao.class).save(datasource);

        this.sqlQuery = sleepSqlQuery(datasource);
        getInjector().getInstance(SqlQueryDao.class).save(sqlQuery);

        schedulerService = getInjector().getInstance(SchedulerService.class);
        schedulerService.schedule(sqlQuery);

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }

        User user = userTableUtil.firstRow();
        loginPage.submitForm(user.getUsername(), user.getPassword());
        loginPage.waitForSuccessMessage(user.getUsername());

        TimeUnit.SECONDS.sleep(70);

        page = createPage(ListExecutingSqlQueriesPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        if (!page.isRendered()) {
            fail("Could not render list ongoing SQL queries page");
        }

        List<SqlQueryExecution> models = getInjector().getInstance(SqlQueryExecutionDao.class).getByStatus(ONGOING);
        Collections.sort(models, (o1, o2) -> o1.getExecutionStart().compareTo(o2.getExecutionStart()));

        schedulerService.stopExecution(sqlQuery.getId(), models.get(0).getExecutionId());
    }

    @Test
    public void test() throws InterruptedException {
        FluentWebElement killButton = $("#executingSqlQueriesTable tr td button", 0);
        assertThat(killButton.getText(), is(Messages.KILL_M.toUpperCase()));
        killButton.click();
        TimeUnit.SECONDS.sleep(30);
        assertThat($("#executingSqlQueriesTable tr td p", 0).getText(), is(Messages.KILL_QUERY_NOT_FOUND_M));
    }
}
