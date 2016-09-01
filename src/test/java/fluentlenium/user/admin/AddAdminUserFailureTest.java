package fluentlenium.user.admin;

import dao.UserDao;
import org.junit.Before;
import org.junit.Test;

public class AddAdminUserFailureTest extends AddAdminUserTest {
    @Before
    public void saveUser() {
        getInjector().getInstance(UserDao.class).save(user);
    }

    @Test
    public void test() {
        initPage();
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForFailureMessage(user);
    }
}
