package controllers.fluentlenium;

import controllers.UserController;
import org.junit.Test;

import static controllers.util.HtmlClass.ISA_ERROR_C;
import static controllers.util.HtmlId.ACTION_RESULT_DIALOG_I;
import static controllers.util.HtmlId.CREATE_ADMIN_USER_I;
import static controllers.util.HtmlId.NEXT_ACTION_I;
import static controllers.util.HtmlId.PASSWORD_I;
import static controllers.util.HtmlId.USERNAME_I;
import static controllers.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static controllers.util.Messages.ADMIN_USER_ADDITION_NEXT_STEP_M;
import static controllers.util.Messages.ADMIN_USER_ADDITION_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AddAdminUserFluentLeniumTest extends FluentLeniumTest {
    @Test
    public void test() throws InterruptedException {
        String username = "purvi";
        String password = "password";

        goTo(getServerAddress() + "#onboarding/add-admin-user");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(htmlId(USERNAME_I)).isPresent();

        fill($(htmlId(USERNAME_I))).with(username);
        fill($(htmlId(PASSWORD_I))).with(password);
        click($(htmlId(CREATE_ADMIN_USER_I)));

        String sucMsg = format(ADMIN_USER_ADDITION_SUCCESS_M, username);
        String sucMsgExpr = htmlIdExpression(ACTION_RESULT_DIALOG_I, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(sucMsg);

        assertThat($(sucMsgExpr).getText(), is(sucMsg));
        assertThat($(htmlId(NEXT_ACTION_I)).getText(), is(format(ADMIN_USER_ADDITION_NEXT_STEP_M)));

        click($(htmlId(CREATE_ADMIN_USER_I)));

        String usrExistsMsg = format(ADMIN_USER_ADDITION_FAILURE_M, username);

        String usrExistsMsgExpr = htmlClassExpression(ISA_ERROR_C, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(usrExistsMsgExpr).hasText(usrExistsMsg);

        assertThat(usrExistsMsg, is($(usrExistsMsgExpr).getText()));
    }

    @Test
    public void testNavigation() {
        String username = "purvi";
        String password = "password";

        goTo(getServerAddress() + "#onboarding/add-admin-user");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(htmlId(USERNAME_I)).isPresent();

        fill($(htmlId(USERNAME_I))).with(username);
        fill($(htmlId(PASSWORD_I))).with(password);
        click($(htmlId(CREATE_ADMIN_USER_I)));

        String sucMsg = format(ADMIN_USER_ADDITION_SUCCESS_M, username);
        String sucMsgExpr = htmlIdExpression(ACTION_RESULT_DIALOG_I, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(sucMsg);

        click($(htmlId(NEXT_ACTION_I)));

        await().atMost(TIMEOUT_SECONDS, SECONDS);

        assertThat(url(), is(getServerAddress() + UserController.ONBOARDING_POST_ADMIN_USER_CREATION_ACTION));
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(htmlNamedTextInputExpression("username")).isPresent();
    }
}
