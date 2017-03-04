package com.kwery.tests.dao.userdao;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.user;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class UserDaoGetByIdTest extends RepoDashDaoTestBase {
    protected UserDao userDao;
    private User user;

    @Before
    public void setUpUserDaoGetByIdTest() {
        user = user();
        userDbSetUp(user);
        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() {
        User fromDb = userDao.getById(user.getId());
        assertThat(fromDb, sameBeanAs(user));
        assertThat(userDao.getById(RandomUtils.nextInt(TestUtil.DB_START_ID + 1, Integer.MAX_VALUE)), nullValue());
    }
}
