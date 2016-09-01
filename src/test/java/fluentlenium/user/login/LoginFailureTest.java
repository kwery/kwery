package fluentlenium.user.login;

import org.junit.Test;

public class LoginFailureTest extends LoginTest {
    @Test
    public void test() {
        initPage();
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForFailureMessage();
    }
}
