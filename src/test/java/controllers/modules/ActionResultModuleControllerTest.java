package controllers.modules;

import org.junit.Test;

import static conf.Routes.ACTION_RESULT_COMPONENT_HTML;
import static conf.Routes.ACTION_RESULT_COMPONENT_JS;

public class ActionResultModuleControllerTest extends ModuleControllerTest {
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

}
