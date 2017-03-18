package com.kwery.tests.dao.userdao;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.TestUtil.user;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserDaoQueryTest extends RepoDashDaoTestBase {
    private UserDao userDao;

    private User user;

    @Before
    public void setUp() {
        user = user();
        userDbSetUp(user);

        userDao = getInstance(UserDao.class);
    }

    @Test
    public void testGetUserByEmailAndPassword() {
        assertThat(userDao.getUser(user.getEmail(), user.getPassword()), notNullValue());
        assertThat(userDao.getUser(user.getEmail(), UUID.randomUUID().toString()), nullValue());
        assertThat(userDao.getUser(UUID.randomUUID().toString(), user.getPassword()), nullValue());
        assertThat(userDao.getUser(UUID.randomUUID().toString(), UUID.randomUUID().toString()), nullValue());
    }

    @Test
    public void testGetUserByEmail() {
        assertThat(userDao.getUserByEmail(user.getEmail()), notNullValue());
        assertThat(userDao.getUserByEmail(UUID.randomUUID().toString()), nullValue());
    }
}
