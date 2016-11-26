package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import org.junit.Test;

public class UserAddSuccessUiTest extends UserAddUiTest {
    @Test
    public void testSuccess() throws InterruptedException {
        User newUser = new User();
        newUser.setUsername("user");
        newUser.setPassword("password");

        page.submitForm(newUser.getUsername(), newUser.getPassword());
        page.waitForSuccessMessage(newUser);
    }
}
