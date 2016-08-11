package controllers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import ninja.NinjaDocTester;
import ninja.i18n.Messages;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import java.util.Map;

import static conf.Routes.CREATE_ADMIN_USER;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UserControllerDocTester extends NinjaDocTester {
    @Test
    public void testCreateAdminUserGet() {
        Response response = makeRequest(Request.GET().url(testServerUrl().path(CREATE_ADMIN_USER)));
        Messages messages = getInjector().getInstance(Messages.class);
        assertThat(response.payload, containsString(messages.get("user.name", Optional.absent()).get()));
        assertThat(response.payload, containsString(messages.get("password", Optional.absent()).get()));
        assertThat(response.payload, containsString(messages.get("create", Optional.absent()).get()));
        assertTrue("Response is HTML", ControllerTestUtil.isHtmlResponse(response));
    }

    @Test
    public void testCreateAdminUserPost() {
        Map<String, String> params = ImmutableMap.of(
                "user", "purvi",
                "password", "secret"
        );
        Response response = makeRequest(Request.POST().formParameters(params).url(testServerUrl().path(CREATE_ADMIN_USER)));
        Messages messages = getInjector().getInstance(Messages.class);
        assertThat(response.payload, containsString(messages.get("admin.user.creation.success", Optional.absent()).get()));
        assertTrue("Response is HTML", ControllerTestUtil.isHtmlResponse(response));
    }
}
