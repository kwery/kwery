package controllers;

import com.google.common.collect.ImmutableMap;
import conf.Routes;
import controllers.util.ControllerTestUtil;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import static controllers.util.ControllerTestUtil.isJsonResponse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DatasourceControllerDocTester extends NinjaDocTester {
    @Test
    public void testAddDatasource() {
        Response response = makeRequest(
                Request.POST().url(testServerUrl().path(Routes.ONBOARDING_ADD_DATASOURCE)).formParameters(
                        ImmutableMap.of(
                                "url", "url",
                                "username", "purvi",
                                "password", "password",
                                "label", "test",
                                "type", "MYSQL"
                        )
                )
        );

        assertTrue("Got JSON response for create datasource post request", isJsonResponse(response));
        assertEquals("Got 200 HTTP status code for create datasource post request", 200, response.httpStatus);
    }
}
