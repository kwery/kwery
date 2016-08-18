package controllers.routetest;

import conf.Routes;
import org.junit.Test;

public class IndexControllerRouteTest extends RouteTest {
    @Test
    public void testIndex() {
        this.setUrl(Routes.INDEX);
        this.assertGetHtml();
    }
}
