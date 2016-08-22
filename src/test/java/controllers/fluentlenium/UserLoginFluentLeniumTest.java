package controllers.fluentlenium;

import controllers.util.TestUtil;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;

import static controllers.fluentlenium.utils.InputField.PASSWORD_F;
import static controllers.fluentlenium.utils.InputField.USERNAME_F;
import static controllers.fluentlenium.utils.UrlFragment.LOGIN_U;
import static controllers.util.HtmlClass.ISA_ERROR_C;
import static controllers.util.HtmlClass.ISA_INFO_C;
import static controllers.util.HtmlId.LOGIN_I;
import static controllers.util.Messages.LOGIN_FAILURE_M;
import static controllers.util.Messages.LOGIN_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserLoginFluentLeniumTest extends FluentLeniumTest {
    private User user = TestUtil.user();

    @Before
    public void before() {
        getInjector().getInstance(UserDao.class).save(user);
    }

    @Test
    public void testSuccess() {
        goTo(getServerAddress() + LOGIN_U);
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(htmlId(LOGIN_I))).isPresent();

        fill($(htmlNamedTextInputExpression(USERNAME_F))).with(user.getUsername());
        fill($(htmlNamedPasswordInputExpression(PASSWORD_F))).with(user.getPassword());
        click($(htmlId(LOGIN_I)));

        String successMessage = format(LOGIN_SUCCESS_M, user.getUsername());
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(htmlClass(ISA_INFO_C))).hasText(successMessage);

        assertThat($(htmlClass(ISA_INFO_C)).getText(), is(successMessage));
    }

    @Test
    public void testFailure() {
        goTo(getServerAddress() + LOGIN_U);
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(htmlId(LOGIN_I))).isPresent();

        fill($(htmlNamedTextInputExpression(USERNAME_F))).with(user.getUsername() + "foo");
        fill($(htmlNamedPasswordInputExpression(PASSWORD_F))).with(user.getPassword());
        click($(htmlId(LOGIN_I)));

        String successMessage = format(LOGIN_FAILURE_M, user.getUsername());
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(htmlClass(ISA_ERROR_C))).hasText(successMessage);

        assertThat($(htmlClass(ISA_ERROR_C)).getText(), is(successMessage));
    }
}
