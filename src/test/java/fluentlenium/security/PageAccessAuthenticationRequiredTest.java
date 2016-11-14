package fluentlenium.security;

import fluentlenium.RepoDashFluentLeniumTest;
import fluentlenium.RepoDashPage;
import fluentlenium.datasource.AddDatasourcePage;
import fluentlenium.datasource.ListDatasourcesPage;
import fluentlenium.datasource.UpdateDatasourcePage;
import fluentlenium.onboarding.OnboardingAdminUserAddedPage;
import fluentlenium.onboarding.OnboardingNextStepsPage;
import fluentlenium.sqlquery.AddSqlQueryPage;
import fluentlenium.sqlquery.ListExecutingSqlQueriesPage;
import fluentlenium.sqlquery.ListSqlQueriesPage;
import fluentlenium.sqlquery.ListSqlQueryExecutionPage;
import fluentlenium.sqlquery.UpdateSqlQueryPage;
import fluentlenium.user.ListUsersPage;
import fluentlenium.user.UpdateUserPage;
import fluentlenium.user.admin.AddAdminUserPage;
import fluentlenium.user.login.LoginPage;
import org.fluentlenium.core.FluentPage;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PageAccessAuthenticationRequiredTest extends RepoDashFluentLeniumTest {
    protected static Logger logger = LoggerFactory.getLogger(PageAccessAuthenticationRequiredTest.class);

    protected Map<Class<? extends FluentPage>, Boolean> pageClasses;

    @Before
    public void setUpPageAccessAuthenticationRequiredTest() {
        pageClasses = new HashMap<>();

        //TODO - Uncomment this once the page rendering issue without sleep is resolved
/*        pageClasses.put(OnboardingAdminUserAddedPage.class, false);*/
        pageClasses.put(OnboardingNextStepsPage.class, true);

        pageClasses.put(LoginPage.class, false);

        pageClasses.put(AddAdminUserPage.class, true);
        pageClasses.put(UpdateUserPage.class, true);
        pageClasses.put(ListUsersPage.class, true);

        pageClasses.put(AddDatasourcePage.class, true);
        pageClasses.put(UpdateDatasourcePage.class, true);
        pageClasses.put(ListDatasourcesPage.class, true);

        pageClasses.put(AddSqlQueryPage.class, true);
        pageClasses.put(ListSqlQueriesPage.class, true);
        pageClasses.put(UpdateSqlQueryPage.class, true);
        pageClasses.put(ListSqlQueryExecutionPage.class, true);

        pageClasses.put(ListExecutingSqlQueriesPage.class, true);
    }

    @Test
    public void test() {
        for (Map.Entry<Class<? extends FluentPage>, Boolean> entry : pageClasses.entrySet()) {
            FluentPage page = createPage(entry.getKey());

            logger.trace(">>>>>>>>>>>>>>>>>>>>>>>>Testing page - " + page.getClass());

            page.withDefaultUrl(getServerAddress());
            goTo(page);

            if (entry.getValue()) {
                await().atMost(TIMEOUT_SECONDS, SECONDS).until($("#loginForm")).isDisplayed();
            } else {
                assertThat(((RepoDashPage)page).isRendered(), is(true));
            }
        }
    }
}
