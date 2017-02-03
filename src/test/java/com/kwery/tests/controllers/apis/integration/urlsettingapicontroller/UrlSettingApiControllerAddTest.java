package com.kwery.tests.controllers.apis.integration.urlsettingapicontroller;

import com.kwery.controllers.apis.UrlSettingApiController;
import com.kwery.models.UrlSetting;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Test;

import static com.kwery.models.UrlSetting.URL_SETTING_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static com.kwery.views.ActionResult.Status.success;

public class UrlSettingApiControllerAddTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws Exception {
        UrlSetting urlSetting = domainSettingWithoutId();

        String url = getInjector().getInstance(Router.class).getReverseRoute(UrlSettingApiController.class, "saveDomainSetting");
        String response = ninjaTestBrowser.postJson(getUrl(url), urlSetting);

        assertJsonActionResult(response, new ActionResult(success, ""));

        new DbTableAsserterBuilder(URL_SETTING_TABLE, domainSettingTable(urlSetting)).columnsToIgnore(UrlSetting.ID_COLUMN).build().assertTable();
    }
}
