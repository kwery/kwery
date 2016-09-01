package fluentlenium.user.admin;

import org.junit.Test;
import util.Messages;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AddAdminUserValidationTest extends AddAdminUserTest {
    @Test
    public void test() {
        initPage();
        page.submitForm();
        assertThat(page.usernameValidationErrorMessage(), is(Messages.USERNAME_VALIDATION_M));
        assertThat(page.passwordValidationErrorMessage(), is(Messages.PASSWORD_VALIDATION_M));
    }
}
