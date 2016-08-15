package controllers;

import org.doctester.testbrowser.HttpConstants;
import org.doctester.testbrowser.Response;

public class ControllerTestUtil {
    public static boolean isHtmlResponse(Response r) {
        return  r.headers.get(HttpConstants.HEADER_CONTENT_TYPE).contains("text/html");
    }

    public static boolean isJavascriptResponse(Response r) {
        return r.headers.get(HttpConstants.HEADER_CONTENT_TYPE).contains("text/javascript");
    }

    public static boolean isJsonResponse(Response r) {
        return r.headers.get(HttpConstants.HEADER_CONTENT_TYPE).contains("application/json");
    }
}
