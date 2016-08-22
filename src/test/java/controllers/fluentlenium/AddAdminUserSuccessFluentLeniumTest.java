package controllers.fluentlenium;

import org.junit.Test;

import static controllers.modules.user.addadmin.UserAddAdminModuleController.ONBOARDING_POST_ADMIN_USER_CREATION_ACTION;
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
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(htmlId(USERNAME_I)).isPresent();

        fill($(htmlId(USERNAME_I))).with(username);
        fill($(htmlId(PASSWORD_I))).with(password);
        click($(htmlId(CREATE_ADMIN_USER_I)));

        String sucMsg = format(ADMIN_USER_ADDITION_SUCCESS_M, username);
        String sucMsgExpr = idSel(ACTION_RESULT_DIALOG_I, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(sucMsg);

        assertThat($(sucMsgExpr).getText(), is(sucMsg));
        assertThat($(htmlId(NEXT_ACTION_I)).getText(), is(format(ADMIN_USER_ADDITION_NEXT_STEP_M).toUpperCase()));

        try {
            fill($(htmlId(USERNAME_I))).with(username);
            fail("Action result dialog window is not covering user name text field");
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase(), containsString("cannot focus element"));
        }

        try {
            fill($(htmlId(PASSWORD_I))).with(username);
            fail("Action result dialog window is not covering password text field");
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase(), containsString("cannot focus element"));
        }

        try {
            click($(htmlId(CREATE_ADMIN_USER_I)));
            fail("Action result dialog window is not covering create user button");
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase(), containsString("element is not clickable at point "));
        }

        //TODO - Figure out escape key press

/*        Actions action = new Actions(getDriver());
        action.sendKeys(Keys.ESCAPE);

        $("body").first().getElement().sendKeys(Keys.ESCAPE);

        await().atMost(TIMEOUT_SECONDS, SECONDS);

        assertThat($(htmlClass("ui-dialog")).first().isDisplayed(), is(true));*/

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
        String sucMsgExpr = idSel(ACTION_RESULT_DIALOG_I, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(sucMsg);

        click($(htmlId(NEXT_ACTION_I)));

        await().atMost(TIMEOUT_SECONDS, SECONDS);

        assertThat(url(), is(getServerAddress() + ONBOARDING_POST_ADMIN_USER_CREATION_ACTION));
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(inputTxtSel("username")).isPresent();
    }
}
