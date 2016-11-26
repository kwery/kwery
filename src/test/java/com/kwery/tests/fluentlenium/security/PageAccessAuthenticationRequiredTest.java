package com.kwery.tests.fluentlenium.security;

import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.datasource.DatasourceAddPage;
import com.kwery.tests.fluentlenium.datasource.DatasourceListPage;
import com.kwery.tests.fluentlenium.datasource.UpdateDatasourcePage;
import com.kwery.tests.fluentlenium.onboarding.OnboardingNextStepsPage;
import com.kwery.tests.fluentlenium.sqlquery.SqlQueryAddPage;
import com.kwery.tests.fluentlenium.sqlquery.SqlQueryExecutingListPage;
import com.kwery.tests.fluentlenium.sqlquery.SqlQueryListPage;
import com.kwery.tests.fluentlenium.sqlquery.SqlQueryExecutionListPage;
import com.kwery.tests.fluentlenium.sqlquery.SqlQueryUpdatePage;
import com.kwery.tests.fluentlenium.user.ListUsersPage;
import com.kwery.tests.fluentlenium.user.UpdateUserPage;
import com.kwery.tests.fluentlenium.user.admin.AddAdminUserPage;
import com.kwery.tests.fluentlenium.user.login.LoginPage;
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

        pageClasses.put(DatasourceAddPage.class, true);
        pageClasses.put(UpdateDatasourcePage.class, true);
        pageClasses.put(DatasourceListPage.class, true);

        pageClasses.put(SqlQueryAddPage.class, true);
        pageClasses.put(SqlQueryListPage.class, true);
        pageClasses.put(SqlQueryUpdatePage.class, true);
        pageClasses.put(SqlQueryExecutionListPage.class, true);

        pageClasses.put(SqlQueryExecutingListPage.class, true);
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
