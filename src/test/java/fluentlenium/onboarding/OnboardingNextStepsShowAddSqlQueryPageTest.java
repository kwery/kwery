package fluentlenium.onboarding;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.DatasourceDao;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import org.junit.Before;
import org.junit.Test;
import util.MySqlDocker;

import javax.sql.DataSource;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OnboardingNextStepsShowAddSqlQueryPageTest extends RepoDashFluentLeniumTest {
    protected OnboardingNextStepsPage page;
    protected MySqlDocker mySqlDocker;

    @Before
    public void setUpOnboardingNextStepsShowAddSqlQueryPageTest() throws InterruptedException {
        UserTableUtil userTableUtil = new UserTableUtil();
        DataSource datasource = DbUtil.getDatasource();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(datasource),
                Operations.sequenceOf(
                        userTableUtil.insertOperation()
                )
        );

        dbSetup.launch();

        mySqlDocker = new MySqlDocker();
        mySqlDocker.start();
        getInjector().getInstance(DatasourceDao.class).save(mySqlDocker.datasource());

        LoginPage loginPage = createPage(LoginPage.class);
        loginPage.withDefaultUrl(getServerAddress());
        goTo(loginPage);
        if (!loginPage.isRendered()) {
            fail("Could not render login page");
        }
        loginPage.submitForm(userTableUtil.firstRow().getUsername(), userTableUtil.firstRow().getPassword());
        loginPage.waitForSuccessMessage(userTableUtil.firstRow());

        page = createPage(OnboardingNextStepsPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);

        //TODO - This is a hack, figure out why the page is rendering on it's own
        SECONDS.sleep(10);

        if (!page.isRendered()) {
            fail("Could not render next steps page");
        }
    }

    @Test
    public void test() {
        assertThat(page.isAddDatasourceNextStepVisible(), is(false));
        assertThat(page.isAddSqlQueryNextStepVisible(), is(true));
    }
}
