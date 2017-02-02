package com.kwery.tests.controllers.apis.integration.domainsettingapicontroller;

import com.kwery.controllers.apis.DomainSettingApiController;
import com.kwery.models.DomainSetting;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingDbSetUp;
import static com.kwery.tests.util.TestUtil.domainSetting;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DomainSettingApiControllerGetTest extends AbstractPostLoginApiTest {
    private DomainSetting domainSetting;

    @Before
    public void setUp() {
        domainSetting = domainSetting();
        domainSettingDbSetUp(domainSetting);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(DomainSettingApiController.class, "getDomainSetting");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());

        assertThat(response, hasJsonPath("$.port", is(domainSetting.getPort())));
        assertThat(response, hasJsonPath("$.domain", is(domainSetting.getDomain())));
        assertThat(response, hasJsonPath("$.scheme", is(domainSetting.getScheme())));
        assertThat(response, hasJsonPath("$.id", is(domainSetting.getId())));
    }
}
