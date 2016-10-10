package dao.userdao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.UserDao;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.User;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class UserDaoGetByIdTest extends RepoDashDaoTestBase {
    protected UserTableUtil userTableUtil;
    protected UserDao userDao;

    @Before
    public void setUpUserDaoGetByIdTest() {
        userTableUtil = new UserTableUtil(1);

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                    userTableUtil.insertOperation()
                )
        ).launch();

        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() {
        User user = userDao.getById(1);
        assertThat(user, sameBeanAs(userTableUtil.row(0)));
        assertThat(userDao.getById(2), nullValue());
    }
}
