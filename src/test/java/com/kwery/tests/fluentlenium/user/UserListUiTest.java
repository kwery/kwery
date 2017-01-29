package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.tests.fluentlenium.user.UserListPage.COLUMNS;
import static com.kwery.tests.util.Messages.DELETE_M;
import static com.kwery.tests.util.Messages.USER_NAME_M;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class UserListUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();
    protected LoginRule loginRule = new LoginRule(ninjaServerRule, this);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(loginRule);

    protected UserListPage page;

    protected User user1;

    @Before
    public void before() {
        user1 = new User();
        user1.setId(2);
        user1.setUsername("purvi");
        user1.setPassword("bestDaughter");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(User.TABLE_DASH_REPO_USER)
                .row()
                .column(User.COLUMN_ID, user1.getId())
                        .column(User.COLUMN_USERNAME, user1.getUsername())
                        .column(User.COLUMN_PASSWORD, user1.getPassword())
                .end()
                .build()
        ).launch();

        page = newInstance(UserListPage.class);
        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render login page");
        }
    }

    @Test
    public void test() {
        page.waitForRows(2);

        List<String> headers = page.headers();

        assertThat(headers, hasSize(COLUMNS));

        assertThat(headers.get(0), is(USER_NAME_M));
        assertThat(headers.get(1), is(DELETE_M));

        List<List<String>> rows = page.rows();

        assertThat(rows, hasSize(2));

        List<String> firstRow = rows.get(0);
        User user0 = loginRule.getLoggedInUser();
        assertThat(firstRow.get(0), is(user0.getUsername()));

        List<String> secondRow = rows.get(1);
        assertThat(secondRow.get(0), is(user1.getUsername()));
    }
}
