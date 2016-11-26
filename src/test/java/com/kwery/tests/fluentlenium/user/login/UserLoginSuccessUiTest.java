package com.kwery.tests.fluentlenium.user.login;

import com.kwery.dao.UserDao;
import org.junit.Before;
import org.junit.Test;

public class UserLoginSuccessUiTest extends UserLoginAbstractTest {
    @Before
    public void saveUser() {
        getInjector().getInstance(UserDao.class).save(user);
    }

    @Test
    public void test() {
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForSuccessMessage(user);
    }
}
