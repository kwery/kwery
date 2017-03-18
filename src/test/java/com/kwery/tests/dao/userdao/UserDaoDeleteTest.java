package com.kwery.tests.dao.userdao;

import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static com.kwery.models.User.TABLE_DASH_REPO_USER;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.userTable;
import static com.kwery.tests.util.TestUtil.user;

public class UserDaoDeleteTest extends RepoDashDaoTestBase {
    protected UserDao userDao;
    private User user;

    @Before
    public void setUpUserDaoUpdateTest() {
        user = user();
        userDbSetUp(user);
        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() throws Exception {
        userDao.delete(user.getId());
        new DbTableAsserterBuilder(TABLE_DASH_REPO_USER, userTable(new LinkedList<>())).build().assertTable();
    }
}
