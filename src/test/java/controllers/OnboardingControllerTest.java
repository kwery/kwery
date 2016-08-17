package controllers;

import conf.Routes;
import controllers.util.ControllerTestUtil;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OnboardingControllerTest extends NinjaDocTester {
    @Test
    public void testWelcome() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(Routes.ONBOARDING_WELCOME))
        );

        assertTrue("Got an HTML response for index page request", ControllerTestUtil.isHtmlResponse(response));
        assertEquals("Got 200 HTTP status code for index page request", 200, response.httpStatus);
    }

    @Test
    public void testCreateAdminUserHtml() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(Routes.ONBOARDING_ADD_ADMIN_USER_HTML))
        );

        assertTrue("Got an HTML response for create admin user html component request", ControllerTestUtil.isHtmlResponse(response));
        assertEquals("Got 200 HTTP status code for create admin user html component request", 200, response.httpStatus);
    }

    @Test
    public void testCreateAdminUserJs() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(Routes.ONBOARDING_ADD_ADMIN_USER_JS))
        );

        assertTrue("Got an HTML response for create admin user js component request", ControllerTestUtil.isJavascriptResponse(response));
        assertEquals("Got 200 HTTP status code for create admin user js component request", 200, response.httpStatus);
    }

}
