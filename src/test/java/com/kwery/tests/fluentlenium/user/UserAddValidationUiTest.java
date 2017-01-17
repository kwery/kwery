package com.kwery.tests.fluentlenium.user;

import org.junit.Test;

import static com.kwery.tests.fluentlenium.user.UserAddPage.INPUT_VALIDATION_ERROR_MESSAGE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserAddValidationUiTest extends UserAddUiTest {
    @Test
    public void test() {
        page.submitForm();
        assertThat(page.usernameValidationErrorMessage(), is(INPUT_VALIDATION_ERROR_MESSAGE));
        assertThat(page.passwordValidationErrorMessage(), is(INPUT_VALIDATION_ERROR_MESSAGE));
    }
}
