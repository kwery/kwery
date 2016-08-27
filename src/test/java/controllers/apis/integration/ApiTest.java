package controllers.apis.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.util.Messages;
import models.User;
import ninja.NinjaTest;
import views.ActionResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

public abstract class ApiTest extends NinjaTest {
    protected String getUrl(String path) {
        String a = getServerAddress();
        a = a.substring(0, a.length() - 1);
        return a + path;
    }

    protected ActionResult actionResult(String actionResultJsonResponse) throws IOException {
        return new ObjectMapper().readValue(actionResultJsonResponse, ActionResult.class);
    }

    protected User user(String json) throws IOException {
        return new ObjectMapper().readValue(json, User.class);
    }

    protected void assertSuccess(ActionResult actionResult, String message) {
        assertThat(actionResult.getMessages(), containsInAnyOrder(message));
        assertThat(actionResult.getStatus(), is(success));
    }

    protected void assertFailure(ActionResult actionResult, String message) {
        assertThat(actionResult.getMessages(), containsInAnyOrder(message));
        assertThat(actionResult.getStatus(), is(failure));
    }

    protected void assertFailure(ActionResult actionResult, String... messages) {
        assertThat(actionResult.getMessages(), containsInAnyOrder(messages));
        assertThat(actionResult.getStatus(), is(failure));
    }

    protected void assertFailure(ActionResult actionResult, Map<String, List<String>> fieldMessages) {
        assertThat(actionResult.getStatus(), is(failure));
        assertThat(actionResult.getFieldMessages(), is(fieldMessages));
    }
}
