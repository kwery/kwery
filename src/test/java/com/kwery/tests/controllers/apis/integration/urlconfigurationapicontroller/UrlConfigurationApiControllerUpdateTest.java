package com.kwery.tests.controllers.apis.integration.urlconfigurationapicontroller;

import com.kwery.controllers.apis.UrlConfigurationApiController;
import com.kwery.models.UrlConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.TestUtil;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.UrlConfiguration.URL_CONFIGURATION_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationTable;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static com.kwery.views.ActionResult.Status.success;

public class UrlConfigurationApiControllerUpdateTest extends AbstractPostLoginApiTest {
    private UrlConfiguration urlConfiguration;

    @Before
    public void setUp() {
        urlConfiguration = TestUtil.domainSetting();
        domainConfigurationDbSetUp(urlConfiguration);
    }

    @Test
    public void test() throws Exception {
        UrlConfiguration updatedUrlConfiguration = domainSettingWithoutId();
        updatedUrlConfiguration.setId(urlConfiguration.getId());

        String url = getInjector().getInstance(Router.class).getReverseRoute(UrlConfigurationApiController.class, "saveDomainSetting");
        String response = ninjaTestBrowser.postJson(getUrl(url), updatedUrlConfiguration);

        assertJsonActionResult(response, new ActionResult(success, ""));

        new DbTableAsserterBuilder(URL_CONFIGURATION_TABLE, domainConfigurationTable(updatedUrlConfiguration)).build().assertTable();
    }
}
