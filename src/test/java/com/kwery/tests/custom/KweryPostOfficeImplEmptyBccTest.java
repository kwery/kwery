package com.kwery.tests.custom;

import com.kwery.custom.KweryPostofficeImpl;
import com.kwery.models.EmailConfiguration;
import com.kwery.services.mail.KweryMail;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.WiserRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.subethamail.wiser.WiserMessage;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class KweryPostOfficeImplEmptyBccTest extends RepoDashTestBase {
    @Rule
    public WiserRule wiserRule = new WiserRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private KweryPostofficeImpl kweryPostoffice;
    private EmailConfiguration emailConfiguration;

    @Before
    public void setUp() {
        emailConfiguration = emailConfiguration();
        emailConfiguration.setBcc("");
        emailConfigurationDbSet(emailConfiguration);

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());

        kweryPostoffice = getInstance(KweryPostofficeImpl.class);
    }

    @Test
    public void test() throws Exception {
        KweryMail kweryMail = getInstance(KweryMail.class);
        kweryMail.setFrom("from@getkwery.com");
        kweryMail.setBodyHtml("text");
        kweryMail.addTo("moo@getkwery.com", "roo@getkwery.com");
        kweryMail.addReplyTo("soo@getkwery.com");
        kweryMail.setSubject("test subject");

        kweryPostoffice.send(kweryMail);

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> wiserRule.wiser().getMessages().size() == 2);

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);
        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        List<String> recipients = new LinkedList<>();
        for (Address address : mimeMessage.getAllRecipients()) {
            recipients.add(address.toString());
        }

        assertThat(recipients, containsInAnyOrder("moo@getkwery.com", "roo@getkwery.com"));
    }
}
