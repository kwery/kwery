package com.kwery.tests.controllers.apis.integration.userapicontroller.login;

import com.kwery.models.User;
import com.kwery.tests.controllers.apis.integration.AbstractApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.LOGIN_API;
import static com.kwery.tests.util.Messages.LOGIN_SUCCESS_M;
import static java.text.MessageFormat.format;

public class LoginSuccessTest extends AbstractApiTest {
    protected User user;

    @Before
    public void loginSuccessTestSetup() {
        user = new User();
        user.setId(1);
        user.setUsername("purvi");
        user.setPassword("bestDaughter");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(User.TABLE_DASH_REPO_USER)
                        .row()
                        .column(User.COLUMN_ID, user.getId())
                        .column(User.COLUMN_USERNAME, user.getUsername())
                        .column(User.COLUMN_PASSWORD, user.getPassword())
                        .end()
                        .build()
        ).launch();
    }

    @Test
    public void test() throws IOException {
        assertSuccess(
                actionResult(
                    ninjaTestBrowser.postJson(getUrl(LOGIN_API), user)),
                    format(LOGIN_SUCCESS_M, user.getUsername()
                )
        );
    }
}
