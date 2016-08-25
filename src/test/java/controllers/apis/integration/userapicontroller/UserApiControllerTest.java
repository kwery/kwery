package controllers.apis.integration.userapicontroller;

import controllers.apis.integration.ApiTest;
import controllers.util.TestUtil;
import dao.UserDao;
import models.User;
import org.junit.Before;

import static conf.Routes.ADD_ADMIN_USER_API;
import static conf.Routes.LOGIN_API;
import static conf.Routes.USER;

public abstract class UserApiControllerTest extends ApiTest {
    protected UserDao userDao;

    protected String addAdminUserApi = "";
    protected String loginApi = "";
    protected String getUserApi = "";

    protected User user;

    @Before
    public void before() {
        this.userDao = getInjector().getInstance(UserDao.class);
        addAdminUserApi = getUrl(ADD_ADMIN_USER_API);
        loginApi = getUrl(LOGIN_API);
        getUserApi = getUrl(USER);
        user = TestUtil.user();
    }
}
