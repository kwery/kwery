package controllers.fluentlenium;

import controllers.util.TestUtil;
import dao.UserDao;
import models.User;
import org.junit.Before;

import static controllers.fluentlenium.utils.InputField.PASSWORD_F;
import static controllers.fluentlenium.utils.InputField.USERNAME_F;
import static controllers.fluentlenium.utils.UrlFragment.LOGIN_U;
import static controllers.util.HtmlClass.ISA_INFO_C;
import static controllers.util.HtmlId.LOGIN_I;
import static controllers.util.Messages.LOGIN_SUCCESS_M;
import static controllers.util.TestUtil.user;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PostLoginTest extends FluentLeniumTest {
    @Before
    public void before() {
        User user = user();

        getInjector().getInstance(UserDao.class).save(user);

        goTo(getServerAddress() + LOGIN_U);
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(idSel(LOGIN_I)).isPresent();

        fill($(inTxtSel(USERNAME_F))).with(user.getUsername());
        fill($(inPwdSel(PASSWORD_F))).with(user.getPassword());
        click($(idSel(LOGIN_I)));

        String successMessage = format(LOGIN_SUCCESS_M, user.getUsername());
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(clsSel(ISA_INFO_C)).hasText(successMessage);

        assertThat($(clsSel(ISA_INFO_C)).getText(), is(successMessage));
     }
}
