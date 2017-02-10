package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerGetSmtpConfigurationTest extends AbstractPostLoginApiTest {
    protected SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpMailApiControllerGetSmtpConfigurationTest() throws Exception {
        smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "getSmtpConfiguration");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());

        assertThat(response, hasJsonPath("$.id", is(smtpConfiguration.getId())));
        assertThat(response, hasJsonPath("$.host", is(smtpConfiguration.getHost())));
        assertThat(response, hasJsonPath("$.port", is(smtpConfiguration.getPort())));
        assertThat(response, hasJsonPath("$.ssl", is(smtpConfiguration.isSsl())));
        assertThat(response, hasJsonPath("$.username", is(smtpConfiguration.getUsername())));
        assertThat(response, hasJsonPath("$.password", is(smtpConfiguration.getPassword())));
        assertThat(response, hasJsonPath("$.useLocalSetting", is(smtpConfiguration.isUseLocalSetting())));
    }
}
