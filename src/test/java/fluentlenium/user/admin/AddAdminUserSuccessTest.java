package fluentlenium.user.admin;

import org.junit.Test;

public class AddAdminUserSuccessTest extends AddAdminUserTest {
    @Test
    public void testSuccess() {
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForSuccessMessage(user);
    }
}
