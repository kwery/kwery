package controllers.fluentlenium;

import controllers.util.TestUtil;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;

import static controllers.util.HtmlClass.ISA_ERROR_C;
import static controllers.util.HtmlId.CREATE_ADMIN_USER_I;
import static controllers.util.HtmlId.PASSWORD_I;
import static controllers.util.HtmlId.USERNAME_I;
import static controllers.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class AddAdminUserFailureFluentLeniumTest extends FluentLeniumTest {
    @Before
    public void before() {
        User user = TestUtil.user();
        getInjector().getInstance(UserDao.class).save(TestUtil.user());
    }

    @Test
    public void test() {
        User user = TestUtil.user();

        goTo(getServerAddress() + "#onboarding/add-admin-user");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(htmlId(USERNAME_I)).isPresent();

        fill($(htmlId(USERNAME_I))).with(user.getUsername());
        fill($(htmlId(PASSWORD_I))).with(user.getPassword());
        click($(htmlId(CREATE_ADMIN_USER_I)));

        String usrExistsMsg = format(ADMIN_USER_ADDITION_FAILURE_M, user.getUsername());

        String usrExistsMsgExpr = htmlClassExpression(ISA_ERROR_C, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(usrExistsMsgExpr).hasText(usrExistsMsg);
    }
}
