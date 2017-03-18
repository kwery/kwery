package com.kwery.tests.dao.userdao;

import com.google.common.collect.Lists;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserDaoListTest extends RepoDashDaoTestBase {
    protected UserDao userDao;
    private User user0;
    private User user1;

    @Before
    public void setUpUserDaoListTest() {
        user0 = TestUtil.user();
        DbUtil.userDbSetUp(user0);

        user1 = TestUtil.user();
        DbUtil.userDbSetUp(user1);

        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() {
        List<User> users = userDao.list();
        assertThat(users, hasSize(2));

        List<User> savedUsers = Lists.newArrayList(user0, user1);
        savedUsers.sort(Comparator.comparing(User::getId));

        for (int i = 0; i < users.size(); ++i) {
            assertThat(users.get(i), sameBeanAs(savedUsers.get(i)));
        }
    }
}
