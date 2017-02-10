package com.kwery.tests.controllers.apis.integration.urlconfigurationapicontroller;

import com.kwery.controllers.apis.UrlConfigurationApiController;
import com.kwery.models.UrlConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Test;

import static com.kwery.models.UrlConfiguration.URL_CONFIGURATION_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationTable;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static com.kwery.views.ActionResult.Status.success;

public class UrlConfigurationApiControllerAddTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws Exception {
        UrlConfiguration urlConfiguration = domainSettingWithoutId();

        String url = getInjector().getInstance(Router.class).getReverseRoute(UrlConfigurationApiController.class, "saveDomainSetting");
        String response = ninjaTestBrowser.postJson(getUrl(url), urlConfiguration);

        assertJsonActionResult(response, new ActionResult(success, ""));

        new DbTableAsserterBuilder(URL_CONFIGURATION_TABLE, domainConfigurationTable(urlConfiguration)).columnsToIgnore(UrlConfiguration.ID_COLUMN).build().assertTable();
    }
}
