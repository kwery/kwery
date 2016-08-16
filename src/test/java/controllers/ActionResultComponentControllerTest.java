package controllers;

import conf.Routes;
import controllers.util.ControllerTestUtil;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionResultComponentControllerTest extends NinjaDocTester {
    @Test
    public void testActionResultComponentHtml() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(Routes.ACTION_RESULT_COMPONENT_HTML))
        );
        assertTrue("Got an HTML response for action result component request", ControllerTestUtil.isHtmlResponse(response));
        assertEquals("Got 200 HTTP status code for action result component request", 200, response.httpStatus);
    }

    @Test
    public void testActionResultComponentJs() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(Routes.ACTION_RESULT_COMPONENT_JS))
        );
        assertTrue("Got Javascript response for action result component request", ControllerTestUtil.isJavascriptResponse(response));
        assertEquals("Got 200 HTTP status code for action result component request", 200, response.httpStatus);
    }
}
