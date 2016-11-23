package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_FROM_EMAIL;
import static com.kwery.models.EmailConfiguration.COLUMN_ID;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_SAVED_M;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerSaveEmailConfigurationAddTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        EmailConfiguration e = new EmailConfiguration();
        e.setFrom("from@foo.com");
        e.setBcc("foo@goo.com");
        e.setReplyTo("bar@goo.com");


        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "saveEmailConfiguration");
        String response = ninjaTestBrowser.postJson(getUrl(url), e);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(EMAIL_CONFIGURATION_SAVED_M)));

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(TABLE_EMAIL_CONFIGURATION)
                .with(COLUMN_FROM_EMAIL, e.getFrom())
                .with(COLUMN_BCC, e.getBcc())
                .with(COLUMN_REPLY_TO, e.getReplyTo())
                .add();

        assertDbState(TABLE_EMAIL_CONFIGURATION, builder.build(), COLUMN_ID);
    }
}
