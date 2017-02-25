package com.kwery.tests.fluentlenium.user;

import org.junit.Test;

public class UserAddValidationUiTest extends UserAddUiTest {
    @Test
    public void test() {
        page.submitForm();
        page.assertNonEmptyUsernameValidationErrorMessage();
        page.assertNotEmptyPasswordValidationErrorMessage();
    }
}
