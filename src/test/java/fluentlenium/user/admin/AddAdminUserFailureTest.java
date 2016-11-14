package fluentlenium.user.admin;

import dao.UserDao;
import org.junit.Before;
import org.junit.Test;

public class AddAdminUserFailureTest extends AddAdminUserTest {
    @Test
    public void test() {
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForFailureMessage(user);
    }
}
