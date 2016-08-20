package controllers.apitest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ninja.NinjaTest;
import views.ActionResult;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

public class ApiTest extends NinjaTest {
    protected String getUrl(String path) {
        String a = getServerAddress();
        a = a.substring(0, a.length() - 1);
        return a + path;
    }

    protected ActionResult actionResult(String actionResultJsonResponse) throws IOException {
        return new ObjectMapper().readValue(actionResultJsonResponse, ActionResult.class);
    }

    protected void assertSuccess(ActionResult actionResult, String message) {
        assertThat(actionResult.getMessage(), is(message));
        assertThat(actionResult.getStatus(), is(success));
    }

    protected void assertSuccessNextAction(ActionResult actionResult, String message, String nextActionName, String nextAtion) {
        assertSuccess(actionResult, message);
        assertThat(actionResult.getNextActionName(), is(nextActionName));
        assertThat(actionResult.getNextAction(), is(nextAtion));
    }

    protected void assertFailure(ActionResult actionResult, String message) {
        assertThat(actionResult.getMessage(), is(message));
        assertThat(actionResult.getStatus(), is(failure));
    }
}
