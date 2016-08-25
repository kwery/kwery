package controllers.fluentlenium;

import org.junit.Test;

import static controllers.util.HtmlId.ACTION_RESULT_DIALOG_I;
import static controllers.util.HtmlId.CREATE_ADMIN_USER_I;
import static controllers.util.HtmlId.NEXT_ACTION_I;
import static controllers.util.HtmlId.PASSWORD_I;
import static controllers.util.HtmlId.USERNAME_I;
import static controllers.util.Messages.ADMIN_USER_ADDITION_NEXT_STEP_M;
import static controllers.util.Messages.ADMIN_USER_ADDITION_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AddAdminUserSuccessFluentLeniumTest extends FluentLeniumTest {
    @Test
    public void test() throws InterruptedException {
        String username = "purvi";
        String password = "password";

        goTo(getServerAddress() + "#onboarding/add-admin-user");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(idSel(USERNAME_I)).isPresent();

        fill($(idSel(USERNAME_I))).with(username);
        fill($(idSel(PASSWORD_I))).with(password);
        click($(idSel(CREATE_ADMIN_USER_I)));

        String sucMsg = format(ADMIN_USER_ADDITION_SUCCESS_M, username);
        String sucMsgExpr = idSel(ACTION_RESULT_DIALOG_I, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(sucMsg);

        assertThat($(sucMsgExpr).getText(), is(sucMsg));
        assertThat($(idSel(NEXT_ACTION_I)).getText(), is(format(ADMIN_USER_ADDITION_NEXT_STEP_M).toUpperCase()));

        try {
            fill($(idSel(USERNAME_I))).with(username);
            fail("Action result dialog window is not covering user name text field");
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase(), containsString("cannot focus element"));
        }

        try {
            fill($(idSel(PASSWORD_I))).with(username);
            fail("Action result dialog window is not covering password text field");
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase(), containsString("cannot focus element"));
        }

        try {
            click($(idSel(CREATE_ADMIN_USER_I)));
            fail("Action result dialog window is not covering create user button");
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase(), containsString("element is not clickable at point "));
        }

        //TODO - Figure out escape key press

/*        Actions action = new Actions(getDriver());
        action.sendKeys(Keys.ESCAPE);

        $("body").first().getElement().sendKeys(Keys.ESCAPE);

        await().atMost(TIMEOUT_SECONDS, SECONDS);

        assertThat($(clsSel("ui-dialog")).first().isDisplayed(), is(true));*/

    }

    @Test
    public void testNavigation() {
        String username = "purvi";
        String password = "password";

        goTo(getServerAddress() + "#onboarding/add-admin-user");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(idSel(USERNAME_I)).isPresent();

        fill($(idSel(USERNAME_I))).with(username);
        fill($(idSel(PASSWORD_I))).with(password);
        click($(idSel(CREATE_ADMIN_USER_I)));

        String sucMsg = format(ADMIN_USER_ADDITION_SUCCESS_M, username);
        String sucMsgExpr = idSel(ACTION_RESULT_DIALOG_I, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(sucMsg);

        click($(idSel(NEXT_ACTION_I)));

        await().atMost(TIMEOUT_SECONDS, SECONDS);

        assertThat(url(), is(getServerAddress() + "#user/login"));
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(inTxtSel("username")).isPresent();
    }
}
