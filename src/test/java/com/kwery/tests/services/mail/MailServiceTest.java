package com.kwery.tests.services.mail;

import com.kwery.models.EmailConfiguration;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.WiserRule;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailServiceTest extends RepoDashTestBase {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    protected MailService mailService;
    private EmailConfiguration emailConfiguration;

    @Before
    public void setUpMailServiceTest() {
        mailService = getInstance(MailService.class);

        emailConfiguration = wiserRule.emailConfiguration();
        emailConfiguration.setBcc("secret@getkwery.com");

        emailConfigurationDbSet(emailConfiguration);
        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
    }

    @Test
    public void test() throws Exception {
        KweryMail kweryMail = getInstance(KweryMail.class);
        kweryMail.setBodyText("bodyText");
        kweryMail.setBodyHtml("<span>text</span>");
        kweryMail.setFrom("from@getkwery.com");
        String toEmailAddress = "to@getkwery.com";
        kweryMail.addTo(toEmailAddress);
        kweryMail.setSubject("subject");

        mailService.send(kweryMail);

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());

        List<String> toAddresses = new ArrayList<>(2);

        for (WiserMessage wiserMessage : wiserRule.wiser().getMessages()) {
            MimeMessage mimeMessage = wiserMessage.getMimeMessage();
            MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
            assertThat(mimeMessageParser.getPlainContent(), is(kweryMail.getBodyText()));
            assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(true));
            assertThat(mimeMessageParser.getSubject(), is(kweryMail.getSubject()));
            assertThat(mimeMessageParser.getFrom(), is(emailConfiguration.getFrom()));
            assertThat(mimeMessageParser.getReplyTo(), is(emailConfiguration.getReplyTo()));
            assertThat(mimeMessageParser.getTo(), containsInAnyOrder(new InternetAddress(toEmailAddress)));
            toAddresses.add(wiserMessage.getEnvelopeReceiver());
        }

        assertThat(toAddresses, containsInAnyOrder(toEmailAddress, emailConfiguration.getBcc()));
    }
}
