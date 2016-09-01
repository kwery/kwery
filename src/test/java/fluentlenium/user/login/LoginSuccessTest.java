package fluentlenium.user.login;

import dao.UserDao;
import org.junit.Before;
import org.junit.Test;

public class LoginSuccessTest extends LoginTest {
    @Before
    public void saveUser() {
        getInjector().getInstance(UserDao.class).save(user);
    }

    @Test
    public void test() {
        initPage();
        page.submitForm(user.getUsername(), user.getPassword());
        page.waitForSuccessMessage(user);
    }
}
