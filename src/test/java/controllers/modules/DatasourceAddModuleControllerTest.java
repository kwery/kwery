package controllers.modules;

import com.google.common.collect.ImmutableMap;
import conf.Routes;
import org.junit.Test;

import static conf.Routes.ADD_DATASOURCE_HTML;
import static conf.Routes.ADD_DATASOURCE_JS;

public class DatasourceAddModuleControllerTest extends ModuleControllerTest {
    @Test
    public void testAddDatasource() {
        this.setUrl(Routes.ADD_DATASOURCE_API);
        this.setPostParams(
                ImmutableMap.of(
                        "url", "url",
                        "username", "purvi",
                        "password", "password",
                        "label", "test",
                        "type", "MYSQL"
                )
        );
        this.assertPostJsonPostLogin();
    }

    @Test
    public void testCreateDatasourceHtml() {
        this.setUrl(ADD_DATASOURCE_HTML);
        this.assertGetHtmlPostLogin();
    }

    @Test
    public void testCreateDatasourceJs() {
        this.setUrl(ADD_DATASOURCE_JS);
        this.assertGetJsPostLogin();
    }
}
