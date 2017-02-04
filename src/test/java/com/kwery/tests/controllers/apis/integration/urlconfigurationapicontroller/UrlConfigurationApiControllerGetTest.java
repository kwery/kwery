package com.kwery.tests.controllers.apis.integration.urlconfigurationapicontroller;

import com.kwery.controllers.apis.UrlConfigurationApiController;
import com.kwery.models.UrlConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.domainSetting;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UrlConfigurationApiControllerGetTest extends AbstractPostLoginApiTest {
    private UrlConfiguration urlConfiguration;

    @Before
    public void setUp() {
        urlConfiguration = domainSetting();
        domainConfigurationDbSetUp(urlConfiguration);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(UrlConfigurationApiController.class, "getDomainSetting");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());

        assertThat(response, hasJsonPath("$.port", is(urlConfiguration.getPort())));
        assertThat(response, hasJsonPath("$.domain", is(urlConfiguration.getDomain())));
        assertThat(response, hasJsonPath("$.scheme", is(urlConfiguration.getScheme().name())));
        assertThat(response, hasJsonPath("$.id", is(urlConfiguration.getId())));
    }
}
