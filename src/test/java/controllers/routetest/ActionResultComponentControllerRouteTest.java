package controllers.routetest;

import org.junit.Test;

import static conf.Routes.ACTION_RESULT_COMPONENT_HTML;
import static conf.Routes.ACTION_RESULT_COMPONENT_JS;
import static conf.Routes.ACTION_RESULT_DIALOG_COMPONENT_HTML;
import static conf.Routes.ACTION_RESULT_DIALOG_COMPONENT_JS;

public class ActionResultComponentControllerRouteTest extends RouteTest {
    @Test
    public void testActionResultComponentHtml() {
        this.setUrl(ACTION_RESULT_COMPONENT_HTML);
        this.assertGetHtml();
    }

    @Test
    public void testActionResultComponentJs() {
        this.setUrl(ACTION_RESULT_COMPONENT_JS);
        this.assertGetJs();
    }

    @Test
    public void testActionResultDialogComponentHtml() {
        this.setUrl(ACTION_RESULT_DIALOG_COMPONENT_HTML);
        this.assertGetHtml();
    }

    @Test
    public void testActionResultDialogComponentJs() {
        this.setUrl(ACTION_RESULT_DIALOG_COMPONENT_JS);
        this.assertGetJs();
    }
}
