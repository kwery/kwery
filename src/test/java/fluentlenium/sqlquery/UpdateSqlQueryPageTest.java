package fluentlenium.sqlquery;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.SqlQueryDao;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.UserTableUtil;
import models.Datasource;
import models.SqlQuery;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;
import util.Messages;
import util.MySqlDocker;

import java.util.concurrent.TimeUnit;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static fluentlenium.utils.DbUtil.getDatasource;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static models.SqlQuery.COLUMN_QUERY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class UpdateSqlQueryPageTest extends RepoDashFluentLeniumTest {
    protected MySqlDocker mySqlDocker;
    protected Datasource datasource;
    protected UpdateSqlQueryPage page;

    @Before
    public void setUpUpdateSqlQueryPageTest() {
        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();

        datasource = mySqlDocker.datasource();

        UserTableUtil userTableUtil = new UserTableUtil(0);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation(),
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .values(2, "testDatasource1", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(1, "* * * * *", "selectQuery", "select * from mysql.db", 1)
                                .values(2, "* * * * *", "sleepQuery", "select sleep(86400)", 1)
                                .build()
                )
        ).launch();

        getInjector().getInstance(SchedulerService.class).schedule(getInjector().getInstance(SqlQueryDao.class).getById(1));

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);

        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }

        User user = userTableUtil.row(0);
        loginPage.submitForm(user.getUsername(), user.getPassword());
        loginPage.waitForSuccessMessage(user.getUsername());

        page = createPage(UpdateSqlQueryPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update user page");
        }
    }

    @Test
    public void testUpdateSuccess() {
        page.waitForForm("label", "selectQuery");

        assertThat(page.actionLabel().toLowerCase(), is(Messages.UPDATE_M.toLowerCase()));

        page.fillLabel("foo");
        page.fillQuery("select sleep(86400)");
        page.fillCronExpression("5 * * * *");
        page.selectDatasource(1);
        page.submit();

        page.waitForSuccessMessage();
    }

    @Test
    public void testDuplicateLabel() {
        page.waitForForm("label", "selectQuery");

        assertThat(page.actionLabel().toLowerCase(), is(Messages.UPDATE_M.toLowerCase()));

        page.fillLabel("sleepQuery");
        //TODO - For some reason, label is being sent to the server as null if the other fields are not filled in, debug this
        page.fillQuery("select sleep(86400)");
        page.fillCronExpression("5 * * * *");
        page.selectDatasource(1);
        page.submit();

        page.waitForDuplicateLabelMessage("sleepQuery");
    }

    @After
    public void tearDownUpdateSqlQueryPageTest() {
        mySqlDocker.tearDown();
    }
}
