package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_UPDATED_M;
import static com.kwery.views.ActionResult.Status.success;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerSaveSmtpConfigurationUpdateTest extends AbstractPostLoginApiTest {
    protected int smtpDetailId = 1;

    protected SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpMailApiControllerSaveSmtpConfigurationUpdateTest() {
        smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setId(smtpDetailId);
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
    public void test() throws DatabaseUnitException, SQLException, IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "saveSmtpConfiguration");
        String response = ninjaTestBrowser.postJson(getUrl(url), smtpConfiguration);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(SMTP_CONFIGURATION_UPDATED_M)));

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                .with(SmtpConfiguration.COLUMN_ID, smtpConfiguration.getId())
                .with(SmtpConfiguration.COLUMN_HOST, smtpConfiguration.getHost())
                .with(SmtpConfiguration.COLUMN_PORT, smtpConfiguration.getPort())
                .with(SmtpConfiguration.COLUMN_SSL, smtpConfiguration.isSsl())
                .with(SmtpConfiguration.COLUMN_USERNAME, smtpConfiguration.getUsername())
                .with(SmtpConfiguration.COLUMN_PASSWORD, smtpConfiguration.getPassword())
                .add();

        assertDbState(SmtpConfiguration.TABLE_SMTP_CONFIGURATION, builder.build());
    }
}
