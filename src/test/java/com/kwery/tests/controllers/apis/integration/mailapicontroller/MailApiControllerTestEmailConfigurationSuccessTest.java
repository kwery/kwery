package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.MailApiController;
import com.kwery.custom.KweryPostofficeImpl;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_FROM_EMAIL;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.models.SmtpConfiguration.COLUMN_HOST;
import static com.kwery.models.SmtpConfiguration.COLUMN_PASSWORD;
import static com.kwery.models.SmtpConfiguration.COLUMN_PORT;
import static com.kwery.models.SmtpConfiguration.COLUMN_SSL;
import static com.kwery.models.SmtpConfiguration.COLUMN_USERNAME;
import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.util.Messages.EMAIL_TEST_BODY_M;
import static com.kwery.tests.util.Messages.EMAIL_TEST_SUBJECT_M;
import static com.kwery.tests.util.Messages.EMAIL_TEST_SUCCESS_M;
import static com.kwery.views.ActionResult.Status.success;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerTestEmailConfigurationSuccessTest extends AbstractPostLoginApiTest {
    protected EmailConfiguration emailConfiguration;

    @Before
    public void setUpMailApiControllerTestEmailConfigurationSuccessTest() {
        emailConfiguration = new EmailConfiguration();
        emailConfiguration.setReplyTo("reply-to@getkwery.com");
        emailConfiguration.setFrom("kwery@getkwery.com");
        emailConfiguration.setBcc("secret@getkwery.com");

        SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setHost("mail.getkwery.com");
        smtpConfiguration.setPort(456);
        smtpConfiguration.setSsl(true);
        smtpConfiguration.setUsername("username");
        smtpConfiguration.setPassword("password");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(
                                TABLE_EMAIL_CONFIGURATION
                        ).row()
                                .column(EmailConfiguration.COLUMN_ID, 1)
                                .column(COLUMN_FROM_EMAIL, emailConfiguration.getFrom())
                                .column(COLUMN_BCC, emailConfiguration.getBcc())
                                .column(COLUMN_REPLY_TO, emailConfiguration.getReplyTo())
                                .end()
                                .build(),
                        insertInto(
                                TABLE_SMTP_CONFIGURATION
                        ).row()
                                .column(SmtpConfiguration.COLUMN_ID, 1)
                                .column(COLUMN_HOST, smtpConfiguration.getHost())
                                .column(COLUMN_PORT, smtpConfiguration.getPort())
                                .column(COLUMN_SSL, smtpConfiguration.isSsl())
                                .column(COLUMN_USERNAME, smtpConfiguration.getUsername())
                                .column(COLUMN_PASSWORD, smtpConfiguration.getPassword())
                                .end()
                                .build()
                )

        ).launch();
    }

    @Test
    public void test() {
        String toEmail = "to@getkwery.com";

        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "testEmailConfiguration",
                ImmutableMap.of("toEmail", toEmail));

        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(EMAIL_TEST_SUCCESS_M)));

        Mail mail = ((PostofficeMockImpl)getInjector().getInstance(Postoffice.class)).getLastSentMail();

        assertThat(mail.getSubject(), is(EMAIL_TEST_SUBJECT_M));
        assertThat(mail.getBodyText(), is(EMAIL_TEST_BODY_M));
        assertThat(mail.getTos(), containsInAnyOrder(toEmail));
    }
}
