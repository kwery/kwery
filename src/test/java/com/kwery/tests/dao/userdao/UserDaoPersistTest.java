package com.kwery.tests.dao.userdao;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static com.kwery.models.User.TABLE_DASH_REPO_USER;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userTable;
import static com.kwery.tests.util.TestUtil.userWithoutId;
import static org.junit.Assert.assertTrue;

public class UserDaoPersistTest extends RepoDashDaoTestBase {
    private UserDao userDao;

    @Before
    public void before() {
        userDao = getInstance(UserDao.class);
    }

    @Test
    public void testPersist() throws Exception {
        User user = userWithoutId();

        DozerBeanMapper mapper = new DozerBeanMapper();
        User expected = mapper.map(user, User.class);

        userDao.save(user);
        expected.setId(user.getId());

        new DbTableAsserterBuilder(TABLE_DASH_REPO_USER, userTable(expected)).build().assertTable();
    }

    @Test
    public void testUniqueUser() {
        User user = userWithoutId();
        userDao.save(user);

        User newUser = userWithoutId();
        newUser.setEmail(user.getEmail());

        try {
            userDao.save(newUser);
        } catch (PersistenceException e) {
            if (!(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
                assertTrue("Unique user condition failed", false);
            }
        }
    }
}
