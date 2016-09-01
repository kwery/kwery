package fluentlenium.user.admin;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AddAdminUserSuccessModalDialogTest extends AddAdminUserTest {
    @Test
    public void test() {
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForSuccessMessage(user);
        try {
            page.submitForm(user.getUsername(), user.getPassword());
            fail("Action result dialog window is not covering user name text field");
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase(), containsString("cannot focus element"));
        }
    }
}
