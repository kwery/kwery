package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.google.common.collect.ImmutableMap;
import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.WiserRule;
import ninja.Router;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.util.Messages.*;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.kwery.views.ActionResult.Status.success;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerTestEmailConfigurationSuccessTest extends AbstractPostLoginApiTest {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    protected EmailConfiguration emailConfiguration;

    @Before
    public void setUpMailApiControllerTestEmailConfigurationSuccessTest() {
        emailConfiguration = new EmailConfiguration();
        emailConfiguration.setReplyTo("reply-to@getkwery.com");
        emailConfiguration.setFrom("kwery@getkwery.com");
        emailConfiguration.setBcc("secret@getkwery.com");
        emailConfiguration.setId(dbId());
        emailConfigurationDbSet(emailConfiguration);

        DbUtil.smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
    }

    @Test
    public void test() throws Exception {
        String toEmail = "to@getkwery.com";

        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "testEmailConfiguration",
                ImmutableMap.of("toEmail", toEmail));

        String response = ninjaTestBrowser.postJson(getUrl(url), new HashMap<>());

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(EMAIL_TEST_SUCCESS_M)));

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());

        List<String> toAddresses = new ArrayList<>(2);

        for (WiserMessage wiserMessage : wiserRule.wiser().getMessages()) {
            MimeMessage mimeMessage = wiserMessage.getMimeMessage();
            MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();

            String html = mimeMessageParser.getHtmlContent();

            Document document = Jsoup.parse(html);
            assertThat(document.select(".footer-t").get(0).text(), is("Email sent by Kwery"));
            assertThat(document.select(".kwery-link-t").get(0).attr("href"), is("http://getkwery.com/"));

            assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(true));
            assertThat(mimeMessageParser.getSubject(), is(EMAIL_TEST_SUBJECT_M));
            assertThat(mimeMessageParser.getFrom(), is(emailConfiguration.getFrom()));
            assertThat(mimeMessageParser.getReplyTo(), is(emailConfiguration.getReplyTo()));
            assertThat(mimeMessageParser.getTo(), containsInAnyOrder(new InternetAddress(toEmail)));
            toAddresses.add(wiserMessage.getEnvelopeReceiver());
        }

        assertThat(toAddresses, containsInAnyOrder(toEmail, emailConfiguration.getBcc()));
    }
}
