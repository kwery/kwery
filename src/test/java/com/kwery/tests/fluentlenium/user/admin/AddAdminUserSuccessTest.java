package com.kwery.tests.fluentlenium.user.admin;

import com.kwery.models.User;
import org.junit.Test;

public class AddAdminUserSuccessTest extends AddAdminUserTest {
    @Test
    public void testSuccess() throws InterruptedException {
        User newUser = new User();
        newUser.setUsername("user");
        newUser.setPassword("password");

        page.submitForm(newUser.getUsername(), newUser.getPassword());
        page.waitForSuccessMessage(newUser);
    }
}
