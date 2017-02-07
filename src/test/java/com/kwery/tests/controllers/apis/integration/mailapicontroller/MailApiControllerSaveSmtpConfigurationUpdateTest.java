package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationTable;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_UPDATED_M;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerSaveSmtpConfigurationUpdateTest extends AbstractPostLoginApiTest {
    protected SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpMailApiControllerSaveSmtpConfigurationUpdateTest() {
        smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "saveSmtpConfiguration");

        SmtpConfiguration updated = TestUtil.smtpConfigurationWithoutId();
        updated.setId(smtpConfiguration.getId());

        String response = ninjaTestBrowser.postJson(getUrl(url), updated);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(SMTP_CONFIGURATION_UPDATED_M)));

        new DbTableAsserterBuilder(TABLE_SMTP_CONFIGURATION, smtpConfigurationTable(updated)).build().assertTable();
    }
}
