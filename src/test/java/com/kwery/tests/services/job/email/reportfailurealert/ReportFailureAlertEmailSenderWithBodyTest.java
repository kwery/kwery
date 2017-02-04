package com.kwery.tests.services.job.email.reportfailurealert;

import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.UrlConfiguration;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.util.Messages.REPORT_GENERATION_FAILURE_ALERT_EMAIL_BODY_M;
import static com.kwery.tests.util.TestUtil.domainSetting;
import static org.exparity.hamcrest.beans.TheSameAs.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class ReportFailureAlertEmailSenderWithBodyTest extends ReportFailureAlertEmailSenderAbstractTest {
    private DomainConfigurationDao domainConfigurationDao;

    @Before
    public void seUp() {
        UrlConfiguration urlConfiguration = domainSetting();
        domainConfigurationDbSetUp(urlConfiguration);

        super.setUp();

        domainConfigurationDao = getInstance(DomainConfigurationDao.class);
    }

    @Test
    public void test() {
        emailSender.send(jobExecutionModel);

        KweryMail kweryMail = getInstance(KweryMail.class);
        kweryMail.setSubject(expectedSubject());

        UrlConfiguration urlConfiguration = domainConfigurationDao.get().get(0);

        String url = urlConfiguration.getScheme() + "://" + urlConfiguration.getDomain() + ":" + urlConfiguration.getPort();

        String body = String.format("<a href='%s/#report/%d/execution/%s'>%s</a>",
                url, jobExecutionModel.getJobModel().getId(), jobExecutionModel.getExecutionId(), REPORT_GENERATION_FAILURE_ALERT_EMAIL_BODY_M);

        kweryMail.setBodyHtml(body);

        MailService mailService = getInstance(MailService.class);
        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();

        assertThat(mail, theSameBeanAs(kweryMail));
    }
}
