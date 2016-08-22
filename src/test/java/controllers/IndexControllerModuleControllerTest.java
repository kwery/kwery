package controllers;

import conf.Routes;
import controllers.modules.ModuleControllerTest;
import org.junit.Test;

public class IndexControllerModuleControllerTest extends ModuleControllerTest {
    @Test
    public void testIndex() {
        this.setUrl(Routes.INDEX);
        this.assertGetHtml();
    }
}
