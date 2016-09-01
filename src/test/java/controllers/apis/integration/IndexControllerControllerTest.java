package controllers.apis.integration;

import conf.Routes;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.HttpConstants;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IndexControllerControllerTest extends NinjaDocTester {
    @Test
    public void testIndex() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(Routes.INDEX))
        );
        assertThat(response.headers.get(HttpConstants.HEADER_CONTENT_TYPE).contains("text/html"), is(true));
        assertThat(response.httpStatus, is(200));
    }
}
