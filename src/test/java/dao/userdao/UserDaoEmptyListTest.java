package dao.userdao;

import dao.UserDao;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserDaoEmptyListTest extends RepoDashDaoTestBase {
    protected UserDao userDao;

    @Before
    public void setUpUserDaoEmptyListTest() {
        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() {
        assertThat(userDao.list(), hasSize(0));
    }
}
