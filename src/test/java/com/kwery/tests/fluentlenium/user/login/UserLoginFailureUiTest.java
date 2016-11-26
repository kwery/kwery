package com.kwery.tests.fluentlenium.user.login;

import org.junit.Test;

public class UserLoginFailureUiTest extends UserLoginAbstractTest {
    @Test
    public void test() {
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForFailureMessage();
    }
}
