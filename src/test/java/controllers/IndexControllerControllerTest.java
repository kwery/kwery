package controllers;

import conf.Routes;
import org.junit.Test;

public class IndexControllerControllerTest extends ControllerTest {
    @Test
    public void testIndex() {
        this.setUrl(Routes.INDEX);
        this.assertGetHtml();
    }
}
