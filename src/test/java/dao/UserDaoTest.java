package dao;

import models.User;
import ninja.NinjaDaoTestBase;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.junit.Assert.assertTrue;

public class UserDaoTest extends NinjaDaoTestBase {
    private UserDao userDao;

    @Before
    public void before() {
        userDao = getInstance(UserDao.class);
    }

    @Test
    public void testPersist() {
        User user = new User();
        user.setUsername("purvi");
        user.setPassword("password");

        userDao.save(user);

        Integer id = user.getId();

        assertTrue("Persisted user has id", id != null && id != 0 && id instanceof Number);
    }

    @Test
    public void testUniqueUser() {
        User user = new User();
        user.setUsername("purvi");
        user.setPassword("password");
        userDao.save(user);

        User newUser = new User();
        newUser.setUsername("purvi");
        newUser.setPassword("password");

        try {
            userDao.save(newUser);
        } catch (PersistenceException e) {
            if (!(e.getCause() instanceof ConstraintViolationException)) {
                assertTrue("Unique user condition failed", false);
            }
        }
    }
}
