package controllers.fluentlenium;

import org.junit.Test;

import static controllers.util.HtmlClass.ISA_ERROR_C;
import static controllers.util.HtmlClass.ISA_INFO_C;
import static controllers.util.HtmlId.CREATE_ADMIN_USER_I;
import static controllers.util.HtmlId.PASSWORD_I;
import static controllers.util.HtmlId.USERNAME_I;
import static controllers.util.Messages.ADMIN_USER_ADDITION_FAILURE_M;
import static controllers.util.Messages.ADMIN_USER_ADDITION_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

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
        String sucMsgExpr = htmlClassExpression(ISA_INFO_C, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(sucMsg);

        assertEquals("User creation success message", sucMsg, $(sucMsgExpr).getText());

        click($(htmlId(CREATE_ADMIN_USER_I)));

        String usrExistsMsg = format(ADMIN_USER_ADDITION_FAILURE_M, username);

        String usrExistsMsgExpr = htmlClassExpression(ISA_ERROR_C, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(usrExistsMsgExpr).hasText(usrExistsMsg);

        assertEquals("User already exists message", usrExistsMsg, $(usrExistsMsgExpr).getText());
    }
}
