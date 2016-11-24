package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.jayway.jsonpath.matchers.JsonPathMatchers;
import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_FROM_EMAIL;
import static com.kwery.models.EmailConfiguration.COLUMN_ID;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_SAVED_M;
import static com.kwery.views.ActionResult.Status.success;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerSaveEmailConfigurationUpdateTest extends AbstractPostLoginApiTest {
    protected int id = 1;

    @Before
    public void setUpMailApiControllerSaveEmailConfigurationUpdateTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                insertInto(
                        TABLE_EMAIL_CONFIGURATION
                )
                        .row()
                        .column(COLUMN_ID, id)
                        .column(COLUMN_FROM_EMAIL, "from@foo.com")
                        .column(COLUMN_REPLY_TO, "foo@bar.com")
                        .column(COLUMN_BCC, "bar@goo.com")
                        .end()
                        .build()
        ).launch();
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "saveEmailConfiguration");

        EmailConfiguration e = new EmailConfiguration();
        e.setId(id);
        e.setFrom("foo@goo.com");
        e.setBcc("bar@goo.com");
        e.setReplyTo("moo@cho.com");

        String response = ninjaTestBrowser.postJson(getUrl(url), e);

        assertThat(response, JsonPathMatchers.isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(EMAIL_CONFIGURATION_SAVED_M)));


        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(TABLE_EMAIL_CONFIGURATION)
                .with(COLUMN_ID, e.getId())
                .with(COLUMN_FROM_EMAIL, e.getFrom())
                .with(COLUMN_BCC, e.getBcc())
                .with(COLUMN_REPLY_TO, e.getReplyTo())
                .add();

        assertDbState(TABLE_EMAIL_CONFIGURATION, builder.build());
    }
}
