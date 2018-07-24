package com.kwery.tests.custom;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.kwery.custom.KweryPostofficeImpl;
import com.kwery.models.EmailConfiguration;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.KweryMailAttachment;
import com.kwery.services.mail.KweryMailAttachmentImpl;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.tests.util.WiserRule;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.subethamail.wiser.WiserMessage;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KweryPostOfficeImplTest extends RepoDashTestBase {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private KweryPostofficeImpl kweryPostoffice;
    private EmailConfiguration emailConfiguration;

    @Before
    public void setUp() {
        emailConfiguration = emailConfiguration();
        emailConfiguration.setBcc("foo@getkwery.com,boo@getkwery.com");
        emailConfigurationDbSet(emailConfiguration);

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());

        kweryPostoffice = getInstance(KweryPostofficeImpl.class);
    }

    @Test
    public void test() throws Exception {
        KweryMail kweryMail = getInstance(KweryMail.class);
        kweryMail.setFrom("from@getkwery.com");
        kweryMail.setBodyHtml("text");
        kweryMail.addBcc("joo@getkwery.com", "zoo@getkwery.com");
        kweryMail.addTo("moo@getkwery.com", "roo@getkwery.com");
        kweryMail.addReplyTo("soo@getkwery.com");
        kweryMail.setSubject("test subject");

        File attachment0 = temporaryFolder.newFile("attachment0.txt");
        Files.write("this text0", attachment0, Charset.defaultCharset());
        KweryMailAttachment kweryMailAttachment0 = new KweryMailAttachmentImpl();
        kweryMailAttachment0.setDescription("");
        kweryMailAttachment0.setFile(attachment0);
        kweryMailAttachment0.setName("attachment0");

        File attachment1 = temporaryFolder.newFile("attachment1.txt");
        Files.write("this text1", attachment1, Charset.defaultCharset());
        KweryMailAttachment kweryMailAttachment1 = new KweryMailAttachmentImpl();
        kweryMailAttachment1.setDescription("");
        kweryMailAttachment1.setFile(attachment1);
        kweryMailAttachment1.setName("attachment1");

        kweryMail.setAttachments(ImmutableList.of(kweryMailAttachment0, kweryMailAttachment1));

        kweryPostoffice.send(kweryMail);

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> wiserRule.wiser().getMessages().size() == 6);

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);
        MimeMessage mimeMessage = wiserMessage.getMimeMessage();

        assertThat(mimeMessage.getSubject(), is("test subject"));

        assertThat(mimeMessage.getFrom()[0].toString(), is(emailConfiguration.getFrom()));


        assertThat(mimeMessage.getReplyTo().length, is(1));
        assertThat(mimeMessage.getReplyTo()[0].toString(), is(emailConfiguration.getReplyTo()));

        List<String> allRecipients = new LinkedList<>();
        for (WiserMessage message : wiserRule.wiser().getMessages()) {
            allRecipients.add(message.getEnvelopeReceiver());
        }

        assertThat(allRecipients,
                containsInAnyOrder("moo@getkwery.com", "roo@getkwery.com", "joo@getkwery.com", "zoo@getkwery.com", "foo@getkwery.com", "boo@getkwery.com"));

        List<String> recipients = new LinkedList<>();
        for (Address address : mimeMessage.getAllRecipients()) {
            recipients.add(address.toString());
        }

        assertThat(recipients, containsInAnyOrder("moo@getkwery.com", "roo@getkwery.com"));


        MimeMessageParser mimeMessageParser0 = new MimeMessageParser(mimeMessage).parse();
        DataSource dataSource0 = mimeMessageParser0.findAttachmentByName("attachment0");
        assertThat(TestUtil.toString(dataSource0).replaceAll("\r\n", "\n").trim(),
                is(TestUtil.toString(attachment0)));

        MimeMessageParser mimeMessageParser1 = new MimeMessageParser(mimeMessage).parse();
        DataSource dataSource1 = mimeMessageParser1.findAttachmentByName("attachment1");
        assertThat(TestUtil.toString(dataSource1).replaceAll("\r\n", "\n").trim(),
                is(TestUtil.toString(attachment1)));

        assertThat(mimeMessage.getContentType(), startsWith("multipart/mixed"));
    }
}
