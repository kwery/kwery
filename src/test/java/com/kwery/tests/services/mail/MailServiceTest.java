package com.kwery.tests.services.mail;

import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import com.kwery.tests.util.RepoDashTestBase;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Before;
import org.junit.Test;

import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class MailServiceTest extends RepoDashTestBase {
    protected MailService mailService;

    @Before
    public void setUpMailServiceTest() {
        mailService = getInstance(MailService.class);
    }

    @Test
    public void test() throws Exception {
        KweryMail kweryMail = getInstance(KweryMail.class);
        kweryMail.setBodyText("bodyText");
        kweryMail.setBodyHtml("<span>text</span>");
        kweryMail.setFrom("from@getkwery.com");
        kweryMail.addTo("to@getkwery.com");
        kweryMail.setSubject("subject");

        mailService.send(kweryMail);

        assertThat(((PostofficeMockImpl)mailService.getPostoffice()).getLastSentMail(), theSameBeanAs(kweryMail));
    }
}
