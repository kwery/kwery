package com.kwery.tests.services.job.email.reportfailurealert;

import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.UrlConfiguration;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static com.kwery.tests.util.TestUtil.domainSetting;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportFailureAlertEmailSenderWithBodyTest extends ReportFailureAlertEmailSenderAbstractTest {
    private DomainConfigurationDao domainConfigurationDao;

    @Before
    public void setUp() {
        UrlConfiguration urlConfiguration = domainSetting();
        domainConfigurationDbSetUp(urlConfiguration);

        super.setUp();

        domainConfigurationDao = getInstance(DomainConfigurationDao.class);
    }

    @Test
    public void test() throws Exception {
        emailSender.send(jobExecutionModel);

        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());

        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();

        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        String htmlContent = mimeMessageParser.getHtmlContent();

        assertContent(htmlContent);
        assertAlertFooter(htmlContent);

        assertThat(mimeMessageParser.getSubject(), is(expectedSubject()));
    }

    public void assertContent(String html) {
        Document doc = Jsoup.parse(html);
        assertThat(doc.select(".alert-t").get(0).text(), is(String.format("Report \"%s\" generation failed, click here to view details.", jobExecutionModel.getJobModel().getTitle())));

        UrlConfiguration urlConfiguration = domainConfigurationDao.get().get(0);
        String url = urlConfiguration.getScheme() + "://" + urlConfiguration.getDomain() + ":" + urlConfiguration.getPort()
                + "/#report/" + jobExecutionModel.getJobModel().getId() + "/execution/" + jobExecutionModel.getExecutionId();

        assertThat(doc.select(".report-link-t").get(0).attr("href"), is(url));
    }
}
