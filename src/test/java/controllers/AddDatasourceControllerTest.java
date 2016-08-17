package controllers;

import controllers.util.ControllerTestUtil;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import static conf.Routes.ONBOARDING_ADD_DATASOURCE_HTML;
import static conf.Routes.ONBOARDING_ADD_DATASOURCE_JS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddDatasourceControllerTest extends NinjaDocTester {
    @Test
    public void testCreateDatasourceHtml() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(ONBOARDING_ADD_DATASOURCE_HTML))
        );

        assertTrue("Got HTML response for create data source html component request", ControllerTestUtil.isHtmlResponse(response));
        assertEquals("Got 200 HTTP status code for create data source HTML component request", 200, response.httpStatus);
    }

    @Test
    public void testCreateDatasourceJs() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(ONBOARDING_ADD_DATASOURCE_JS))
        );

        assertTrue("Got Javascript response for create data source JS component request", ControllerTestUtil.isJavascriptResponse(response));
        assertEquals("Got 200 HTTP status code for create data source JS component request", 200, response.httpStatus);
    }
}
