package dao;

import models.User;
import ninja.NinjaDaoTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserDaoQueryTest extends NinjaDaoTestBase {
    private UserDao userDao;

    private String savedUsername = "purvi";
    private String password = "password";
    private String notSaveUsername = "foo";

    @Before
    public void before() {
        userDao = getInstance(UserDao.class);
        User user = new User();
        user.setUsername(savedUsername);
        user.setPassword(password);
        userDao.save(user);
    }

    @Test
    public void testQueryByUserName() {
        Assert.assertNotNull(String.format("Getting user entity with user name %s", savedUsername), userDao.getByUsername(savedUsername));
        Assert.assertNull(String.format("Getting user entity with user name %s", notSaveUsername), userDao.getByUsername(notSaveUsername));
    }
}
