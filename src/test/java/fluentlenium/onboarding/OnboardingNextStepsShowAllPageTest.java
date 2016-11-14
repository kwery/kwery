package fluentlenium.onboarding;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.user.login.LoginPage;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import org.junit.Before;
import org.junit.Test;
import util.Messages;

import javax.sql.DataSource;

import static fluentlenium.onboarding.OnboardingNextStepsPage.NEXT_STEPS_COUNT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static util.Messages.NEXT_STEP_ADD_DATASOURCE_M;
import static util.Messages.NEXT_STEP_ADD_SQL_QUERY_M;
import static util.Messages.NEXT_STEP_HEADER_M;

public class OnboardingNextStepsShowAllPageTest extends RepoDashFluentLeniumTest {
    protected OnboardingNextStepsPage page;

    @Before
    public void setUpOnboardingNextStepsShowAllPageTest() throws InterruptedException {
        UserTableUtil userTableUtil = new UserTableUtil();
        DataSource datasource = DbUtil.getDatasource();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(datasource),
                Operations.sequenceOf(
                        userTableUtil.insertOperation()
                )
        );

        dbSetup.launch();

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
        assertThat(page.nextStepsCount(), is(NEXT_STEPS_COUNT));
        assertThat(page.nextStepsHeaderText(), is(NEXT_STEP_HEADER_M));
        assertThat(page.nextStepText(0), is(NEXT_STEP_ADD_DATASOURCE_M));
        assertThat(page.nextStepText(1), is(NEXT_STEP_ADD_SQL_QUERY_M));
    }
}
