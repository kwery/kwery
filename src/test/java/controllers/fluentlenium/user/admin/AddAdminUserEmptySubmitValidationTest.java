package controllers.fluentlenium.user.admin;

import controllers.util.Messages;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AddAdminUserEmptySubmitValidationTest extends AddAdminUserTest {
    @Test
    public void test() {
        initPage();
        page.submitForm();
        assertThat(page.usernameValidationMessage(), is(Messages.USERNAME_VALIDATION_M));
        assertThat(page.passwordValidationMessage(), is(Messages.PASSWORD_VALIDATION_M));
    }
}
