package controllers.apis.integration.userapicontroller.addadmin;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.integration.AbstractApiTest;
import dao.UserDao;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.User;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;

import static conf.Routes.ADD_ADMIN_USER_API;
import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static util.Messages.ADMIN_USER_ADDITION_FAILURE_M;

public class AddAdminUserExistsFailureTest extends AbstractApiTest {
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
