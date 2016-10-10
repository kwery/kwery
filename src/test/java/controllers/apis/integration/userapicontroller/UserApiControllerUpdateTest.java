package controllers.apis.integration.userapicontroller;

import controllers.apis.UserApiController;
import ninja.Router;
import org.junit.Test;
import util.Messages;

import java.text.MessageFormat;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static views.ActionResult.Status.success;

public class UserApiControllerUpdateTest extends AbstractPostLoginApiTest {
    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(
                UserApiController.class, "addAdminUser"
        );

        String updatedPassword = "foo";
        loggedInUser.setPassword(updatedPassword);

        String response = ninjaTestBrowser.postJson(getUrl(url), loggedInUser);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(MessageFormat.format(Messages.USER_UPDATE_SUCCESS_M, loggedInUser.getUsername()))));
    }
}
