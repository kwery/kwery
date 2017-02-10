package com.kwery.tests.services.job.email.reportfailurealert;

import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.UrlConfiguration;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;

import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.util.Messages.REPORT_GENERATION_FAILURE_ALERT_EMAIL_BODY_M;
import static com.kwery.tests.util.TestUtil.domainSetting;
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

        UrlConfiguration urlConfiguration = domainConfigurationDao.get().get(0);

        String url = urlConfiguration.getScheme() + "://" + urlConfiguration.getDomain() + ":" + urlConfiguration.getPort();

        String body = String.format("<a href='%s/#report/%d/execution/%s'>%s</a>",
                url, jobExecutionModel.getJobModel().getId(), jobExecutionModel.getExecutionId(), REPORT_GENERATION_FAILURE_ALERT_EMAIL_BODY_M);

        assertThat(wiserRule.wiser().getMessages(), hasSize(1));

        WiserMessage wiserMessage = wiserRule.wiser().getMessages().get(0);

        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        assertThat(mimeMessageParser.getSubject(), is(expectedSubject()));
        assertThat(mimeMessageParser.getHtmlContent(), is(body));
    }
}
