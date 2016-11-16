package com.kwery.tests.controllers.apis.integration.userapicontroller.addadmin;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.dao.UserDao;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.kwery.models.User;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;

import static com.kwery.conf.Routes.ADD_ADMIN_USER_API;
import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;

public class AddAdminUserExistsFailureTest extends AbstractPostLoginApiTest {
    private UserDao userDao;
    private User user;

    @Before
    public void addAdminUserExistsFailureTestSetup() {
        UserTableUtil userTableUtil = new UserTableUtil();
        DataSource datasource = DbUtil.getDatasource();

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(datasource),
                Operations.sequenceOf(
                        userTableUtil.insertOperation()
                )
        );

        dbSetup.launch();

        user = userTableUtil.firstRow();
        user.setId(null);

        userDao = getInjector().getInstance(UserDao.class);
    }

    @Test
    public void test() throws IOException {
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(getUrl(ADD_ADMIN_USER_API), user)),
                format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername())
        );

        assertThat(userDao.getByUsername(user.getUsername()), notNullValue());
    }
}
