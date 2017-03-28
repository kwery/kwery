package com.kwery.tests.dao.userdao;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertThat;

public class UserDaoListTest extends RepoDashDaoTestBase {
    protected UserDao userDao;
    private User user0;
    private User user1;

    @Before
    public void setUpUserDaoListTest() {
        user0 = TestUtil.user();
        user0.setCreated(System.currentTimeMillis() - 1000);

        DbUtil.userDbSetUp(user0);

        user1 = TestUtil.user();
        user1.setCreated(System.currentTimeMillis());

        DbUtil.userDbSetUp(user1);

        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() {
        List<User> users = userDao.list();
        assertThat(users.get(0), sameBeanAs(user0));
        assertThat(users.get(1), sameBeanAs(user1));
    }
}
