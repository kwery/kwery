package com.kwery.tests.controllers.apis.integration.urlsettingapicontroller;

import com.kwery.controllers.apis.UrlSettingApiController;
import com.kwery.models.UrlSetting;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.TestUtil;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.UrlSetting.URL_SETTING_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static com.kwery.views.ActionResult.Status.success;

public class UrlSettingApiControllerUpdateTest extends AbstractPostLoginApiTest {
    private UrlSetting urlSetting;

    @Before
    public void setUp() {
        urlSetting = TestUtil.domainSetting();
        domainSettingDbSetUp(urlSetting);
    }

    @Test
    public void test() throws Exception {
        UrlSetting updatedUrlSetting = domainSettingWithoutId();
        updatedUrlSetting.setId(urlSetting.getId());

        String url = getInjector().getInstance(Router.class).getReverseRoute(UrlSettingApiController.class, "saveDomainSetting");
        String response = ninjaTestBrowser.postJson(getUrl(url), updatedUrlSetting);

        assertJsonActionResult(response, new ActionResult(success, ""));

        new DbTableAsserterBuilder(URL_SETTING_TABLE, domainSettingTable(updatedUrlSetting)).build().assertTable();
    }
}
