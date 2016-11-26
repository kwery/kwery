package com.kwery.tests.fluentlenium.user;

import org.junit.Test;
import com.kwery.tests.util.Messages;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserAddValidationUiTest extends UserAddUiTest {
    @Test
    public void test() {
        page.submitForm();
        assertThat(page.usernameValidationErrorMessage(), is(Messages.USERNAME_VALIDATION_M));
        assertThat(page.passwordValidationErrorMessage(), is(Messages.PASSWORD_VALIDATION_M));
    }
}
