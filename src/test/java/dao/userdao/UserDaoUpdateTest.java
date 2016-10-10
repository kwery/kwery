package dao.userdao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.CannotModifyUsernameException;
import dao.UserDao;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.User;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserDaoUpdateTest extends RepoDashDaoTestBase {
    protected UserDao userDao;
    protected UserTableUtil userTableUtil;

    @Before
    public void setUpUserDaoUpdateTest() {
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
        User user = userTableUtil.firstRow();
        String updatedPassword = "foo";
        user.setPassword(updatedPassword);

        userDao.update(user);

        User fromDb = userDao.getById(1);

        assertThat(fromDb.getUsername(), is(user.getUsername()));
        assertThat(fromDb.getPassword(), is(updatedPassword));
    }

    @Test(expected = CannotModifyUsernameException.class)
    public void testUpdateUsername() {
        User user = userTableUtil.firstRow();
        user.setUsername("foo");
        userDao.update(user);
    }
}
