package dao.userdao;

import dao.UserDao;
import models.User;
import ninja.NinjaDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertTrue;

public class UserDaoPersistTest extends NinjaDaoTestBase {
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
            if (!(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
                assertTrue("Unique user condition failed", false);
            }
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNullValuesValidation() {
        User invalid = new User();
        userDao.save(invalid);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidFieldLengthValidation() {
        User invalid = new User();
        invalid.setUsername("");
        invalid.setPassword("");
        userDao.save(invalid);
    }
}
