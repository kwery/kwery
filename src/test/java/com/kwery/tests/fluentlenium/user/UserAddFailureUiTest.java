package com.kwery.tests.fluentlenium.user;

import org.junit.Test;

public class UserAddFailureUiTest extends UserAddUiTest {
    @Test
    public void test() {
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForFailureMessage(user);
    }
}
