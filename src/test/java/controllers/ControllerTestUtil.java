package controllers;

import org.doctester.testbrowser.HttpConstants;
import org.doctester.testbrowser.Response;

public class ControllerTestUtil {
    public static boolean isHtmlResponse(Response reponse) {
        return  reponse.headers.get(HttpConstants.HEADER_CONTENT_TYPE).contains("text/html");
    }
}
