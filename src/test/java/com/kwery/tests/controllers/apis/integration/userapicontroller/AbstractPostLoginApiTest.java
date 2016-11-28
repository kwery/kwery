package com.kwery.tests.controllers.apis.integration.userapicontroller;

import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;

import static com.kwery.conf.Routes.LOGIN_API;

public abstract class AbstractPostLoginApiTest extends AbstractApiTest {
    protected User loggedInUser;

    @Before
    public void setupPostLoginApiTest() {
        loggedInUser = new User();
        loggedInUser.setId(1);
        loggedInUser.setUsername("purvi");
        loggedInUser.setPassword("bestDaughter");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(User.TABLE_DASH_REPO_USER)
                .row()
                .column(User.COLUMN_ID, loggedInUser.getId())
                .column(User.COLUMN_USERNAME, loggedInUser.getUsername())
                .column(User.COLUMN_PASSWORD, loggedInUser.getPassword())
                .end()
                .build()
        ).launch();

        ninjaTestBrowser.postJson(getUrl(LOGIN_API), loggedInUser);
    }
}
