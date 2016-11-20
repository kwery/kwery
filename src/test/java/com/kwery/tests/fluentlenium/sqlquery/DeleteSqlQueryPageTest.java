package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.User;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.tests.util.MySqlDocker;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.util.Messages.DELETE_M;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DeleteSqlQueryPageTest extends RepoDashFluentLeniumTest {
    protected ListSqlQueriesPage page;
    protected MySqlDocker mySqlDocker;
    protected List<SqlQuery> queries;

    @Before
    public void setUpDeleteSqlQueryPageTest() throws InterruptedException {
        mySqlDocker = new MySqlDocker();
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

        queries = new ArrayList<>(2);

        SqlQuery sqlQuery0 = TestUtil.listMySqlUserQuery(datasource);
        getInjector().getInstance(SqlQueryDao.class).save(sqlQuery0);
        getInjector().getInstance(SchedulerService.class).schedule(sqlQuery0);
        queries.add(sqlQuery0);

        SqlQuery sqlQuery1 = TestUtil.listMySqlUserQuery(datasource);
        sqlQuery1.setLabel(sqlQuery0.getLabel() + "0");
        getInjector().getInstance(SqlQueryDao.class).save(sqlQuery1);
        getInjector().getInstance(SchedulerService.class).schedule(sqlQuery1);
        queries.add(sqlQuery1);

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

        page = createPage(ListSqlQueriesPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render list SQL queries execution page");
        }
    }

    @Test
    public void test() {
        page.waitForRows(2);
        page.delete(0);

        assertThat(page.deleteLabel(0), is(DELETE_M));

        page.waitForDeleteSuccessMessage(queries.get(0).getLabel());
        List<List<String>> rows = page.rows();
        assertThat(rows, hasSize(1));
        assertThat(rows.get(0).get(0), is(queries.get(1).getLabel()));
    }
}
