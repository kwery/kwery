package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.CompositeOperation;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.EmailConfiguration.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerGetEmailConfigurationTest extends AbstractPostLoginApiTest {
    protected EmailConfiguration e;

    @Before
    public void setUpMailApiControllerGetEmailConfigurationTest() {
        e = new EmailConfiguration();
        e.setId(1);
        e.setFrom("from@foo.com");
        e.setBcc("foo@goo.com");
        e.setReplyTo("bar@moo.com");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                CompositeOperation.sequenceOf(
                        Operations.insertInto(
                                TABLE_EMAIL_CONFIGURATION
                        ).row()
                                .column(COLUMN_ID, e.getId())
                                .column(COLUMN_FROM_EMAIL, e.getFrom())
                                .column(COLUMN_BCC, e.getBcc())
                                .column(COLUMN_REPLY_TO, e.getReplyTo())
                                .end()
                                .build()
                )
        ).launch();
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "getEmailConfiguration");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.id", is(e.getId())));
        assertThat(response, hasJsonPath("$.from", is(e.getFrom())));
        assertThat(response, hasJsonPath("$.bcc", is(e.getBcc())));
        assertThat(response, hasJsonPath("$.replyTo", is(e.getReplyTo())));
    }
}
