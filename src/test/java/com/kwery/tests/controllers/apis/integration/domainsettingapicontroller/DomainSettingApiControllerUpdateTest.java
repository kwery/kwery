package com.kwery.tests.controllers.apis.integration.domainsettingapicontroller;

import com.kwery.controllers.apis.DomainSettingApiController;
import com.kwery.models.DomainSetting;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.TestUtil;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.DomainSetting.DOMAIN_SETTING_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.assertJsonActionResult;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;
import static com.kwery.views.ActionResult.Status.success;

public class DomainSettingApiControllerUpdateTest extends AbstractPostLoginApiTest {
    private DomainSetting domainSetting;

    @Before
    public void setUp() {
        domainSetting = TestUtil.domainSetting();
        domainSettingDbSetUp(domainSetting);
    }

    @Test
    public void test() throws Exception {
        DomainSetting updatedDomainSetting = domainSettingWithoutId();
        updatedDomainSetting.setId(domainSetting.getId());

        String url = getInjector().getInstance(Router.class).getReverseRoute(DomainSettingApiController.class, "saveDomainSetting");
        String response = ninjaTestBrowser.postJson(getUrl(url), updatedDomainSetting);

        assertJsonActionResult(response, new ActionResult(success, ""));

        new DbTableAsserterBuilder(DOMAIN_SETTING_TABLE, domainSettingTable(updatedDomainSetting)).build().assertTable();
    }
}
