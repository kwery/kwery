package com.kwery.tests.controllers.apis.integration.urlsettingapicontroller;

import com.kwery.controllers.apis.UrlSettingApiController;
import com.kwery.models.UrlSetting;
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

public class UrlSettingApiControllerGetTest extends AbstractPostLoginApiTest {
    private UrlSetting urlSetting;

    @Before
    public void setUp() {
        urlSetting = domainSetting();
        domainSettingDbSetUp(urlSetting);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(UrlSettingApiController.class, "getDomainSetting");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());

        assertThat(response, hasJsonPath("$.port", is(urlSetting.getPort())));
        assertThat(response, hasJsonPath("$.domain", is(urlSetting.getDomain())));
        assertThat(response, hasJsonPath("$.scheme", is(urlSetting.getScheme().name())));
        assertThat(response, hasJsonPath("$.id", is(urlSetting.getId())));
    }
}
