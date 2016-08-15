package controllers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import conf.Routes;
import ninja.NinjaDocTester;
import ninja.i18n.Messages;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import java.util.Map;

import static conf.Routes.CREATE_ADMIN_USER;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UserControllerDocTester extends NinjaDocTester {
    @Test
    public void testCreateAdminUser() {
        Response response = makeRequest(
                Request.POST().url(testServerUrl().path(Routes.ONBOARDING_CREATE_ADMIN_USER)).formParameters(
                        ImmutableMap.of(
                                "username", "foo",
                                "password", "password"
                        )
                )
        );

        assertTrue("Got a JSON response for create admin user post request", ControllerTestUtil.isJsonResponse(response));
        assertEquals("Got a 200 HTTP status code for create admin user post request", 200, response.httpStatus);
    }
}
