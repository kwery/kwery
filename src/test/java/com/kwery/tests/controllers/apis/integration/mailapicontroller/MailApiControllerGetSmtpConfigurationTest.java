package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerGetSmtpConfigurationTest extends AbstractPostLoginApiTest {
    protected SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpMailApiControllerGetSmtpConfigurationTest() {
        smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setId(1);
        smtpConfiguration.setHost("foo.com");
        smtpConfiguration.setPort(465);
        smtpConfiguration.setSsl(true);
        smtpConfiguration.setUsername("username");
        smtpConfiguration.setPassword("password");

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                                .row()
                                .column(SmtpConfiguration.COLUMN_ID, smtpConfiguration.getId())
                                .column(SmtpConfiguration.COLUMN_HOST, smtpConfiguration.getHost())
                                .column(SmtpConfiguration.COLUMN_PORT, smtpConfiguration.getPort())
                                .column(SmtpConfiguration.COLUMN_SSL, smtpConfiguration.isSsl())
                                .column(SmtpConfiguration.COLUMN_USERNAME, smtpConfiguration.getUsername())
                                .column(SmtpConfiguration.COLUMN_PASSWORD, smtpConfiguration.getPassword())
                                .end()
                                .build()
                )
        );

        dbSetup.launch();
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
    }
}
