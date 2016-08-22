package controllers.modules;

import controllers.util.ControllerTestUtil;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;

import java.util.Map;

import static controllers.util.TestUtil.COOKIE_STRING;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ModuleControllerTest extends NinjaDocTester {
    protected String url;
    protected Map<String, String> postParams;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getPostParams() {
        return postParams;
    }

    public void setPostParams(Map<String, String> postParams) {
        this.postParams = postParams;
    }

    public void assertGetHtmlPostLogin() {
        Response response = makeRequest(
                Request.GET().addHeader("Cookie", COOKIE_STRING).url(testServerUrl().path(this.url))
        );
        assertThat(ControllerTestUtil.isHtmlResponse(response), is(true));
        assertThat(response.httpStatus, is(200));
    }

    public void assertGetJsPostLogin() {
        Response response = makeRequest(
                Request.GET().addHeader("Cookie", COOKIE_STRING).url(testServerUrl().path(this.url))
        );
        assertThat(ControllerTestUtil.isJavascriptResponse(response), is(true));
        assertThat(response.httpStatus, is(200));
    }

    public void assertGetHtml() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(this.url))
        );
        assertThat(ControllerTestUtil.isHtmlResponse(response), is(true));
        assertThat(response.httpStatus, is(200));
    }

    public void assertGetJs() {
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path(this.url))
        );
        assertThat(ControllerTestUtil.isJavascriptResponse(response), is(true));
        assertThat(response.httpStatus, is(200));
    }

    public void assertPostJson() {
        Response response = makeRequest(Request.POST().url(testServerUrl().path(this.getUrl())).formParameters(this.postParams));
        assertThat(ControllerTestUtil.isJsonResponse(response), is(true));
        assertThat(response.httpStatus, is(200));
    }

    public void assertPostJsonPostLogin() {
        Response response = makeRequest(
                Request.POST().addHeader("Cookies", COOKIE_STRING).url(testServerUrl().path(this.getUrl())).formParameters(this.postParams)
        );
        assertThat(ControllerTestUtil.isJsonResponse(response), is(true));
        assertThat(response.httpStatus, is(200));
    }
}
