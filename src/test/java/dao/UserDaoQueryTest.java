package dao;

import models.User;
import ninja.NinjaDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserDaoQueryTest extends NinjaDaoTestBase {
    private UserDao userDao;

    private String savedUsername = "purvi";
    private String savedPassword = "puttu";

    private String notSavedUsername = "foo";
    private String notSavedPassword = "goo";

    @Before
    public void before() {
        userDao = getInstance(UserDao.class);
        User user = new User();
        user.setUsername(savedUsername);
        user.setPassword(savedPassword);
        userDao.save(user);
    }

    @Test
    public void testGetUserByUsername() {
        assertThat(userDao.getByUsername(savedUsername), notNullValue());
        assertThat(userDao.getByUsername(notSavedUsername), nullValue());
    }

    @Test
    public void testGetUserByUsernameAndPassword() {
        assertThat(userDao.getUser(savedUsername, savedPassword), notNullValue());
        assertThat(userDao.getUser(savedUsername, notSavedPassword), nullValue());
        assertThat(userDao.getUser(notSavedUsername, savedPassword), nullValue());
        assertThat(userDao.getUser(notSavedUsername, notSavedPassword), nullValue());
    }
}
