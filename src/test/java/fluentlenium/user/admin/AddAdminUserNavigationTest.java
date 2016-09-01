package fluentlenium.user.admin;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AddAdminUserNavigationTest extends AddAdminUserTest {
    @Test
    public void test() {
        initPage();
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForSuccessMessage(user);
        assertThat($("#nextAction").getText(), is(page.expectedNextActionName()));
        assertThat(page.nextActionName(), is(page.expectedNextActionName()));
        page.clickNextAction();
        page.waitForNextPage();
        assertThat(url(), is(page.expectedNextActionUrl()));
    }
}
