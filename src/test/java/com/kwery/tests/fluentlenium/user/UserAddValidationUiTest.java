package com.kwery.tests.fluentlenium.user;

import org.junit.Test;

import static com.kwery.tests.util.Messages.PASSWORD_VALIDATION_M;
import static com.kwery.tests.util.Messages.USERNAME_VALIDATION_M;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserAddValidationUiTest extends UserAddUiTest {
    @Test
    public void test() {
        page.submitForm();
        assertThat(page.usernameValidationErrorMessage(), is(USERNAME_VALIDATION_M));
        assertThat(page.passwordValidationErrorMessage(), is(PASSWORD_VALIDATION_M));
    }
}
