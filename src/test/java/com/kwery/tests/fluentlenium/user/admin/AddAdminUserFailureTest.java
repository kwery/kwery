package com.kwery.tests.fluentlenium.user.admin;

import org.junit.Test;

public class AddAdminUserFailureTest extends AddAdminUserTest {
    @Test
    public void test() {
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForFailureMessage(user);
    }
}
