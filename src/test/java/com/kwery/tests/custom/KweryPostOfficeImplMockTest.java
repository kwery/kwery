package com.kwery.tests.custom;

import com.kwery.custom.KweryPostofficeImpl;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.EmailConfigurationService;
import com.kwery.services.mail.KweryMailImpl;
import com.kwery.services.mail.smtp.SmtpService;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.commonsmail.CommonsmailHelper;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KweryPostOfficeImplMockTest {
    @Mock
    protected CommonsmailHelper commonsmailHelper;
    @Mock
    protected SmtpService smtpService;
    @Mock
    protected EmailConfigurationService emailConfigurationService;

    @Test
    public void test() throws Exception {
        Postoffice postoffice = new KweryPostofficeImpl(commonsmailHelper, smtpService, emailConfigurationService);

        SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setHost("mail.getquery.com");
        smtpConfiguration.setPort(456);
        smtpConfiguration.setSsl(true);
        smtpConfiguration.setUsername("user");
        smtpConfiguration.setPassword("password");

        when(smtpService.getSmtpConfiguration()).thenReturn(smtpConfiguration);

        EmailConfiguration emailConfiguration = new EmailConfiguration();
        emailConfiguration.setFrom("from@getquery.com");
        emailConfiguration.setBcc("bcc@getquery.com");
        emailConfiguration.setReplyTo("reply-to@getquery.com");

        when(emailConfigurationService.getEmailConfiguration()).thenReturn(emailConfiguration);

        Mail mail = new KweryMailImpl();
        mail.setBodyText("body test");
        mail.setBodyHtml("body html");
        mail.setFrom("default-from@getquery.com");
        mail.addTo("to@getquery.com");
        mail.addBcc("bcc@getquery.com");
        mail.addReplyTo("defaultreply-to@getquery.com");
        mail.setSubject("test mail");

        MultiPartEmail multiPartEmail = mock(MultiPartEmail.class);

        when(commonsmailHelper.createMultiPartEmailWithContent(mail)).thenReturn(multiPartEmail);

        postoffice.send(mail);

        ArgumentCaptor<String> smtpHost = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> smtpPort = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Boolean> ssl = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> user = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> password = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> debug = ArgumentCaptor.forClass(Boolean.class);

        verify(commonsmailHelper).doSetServerParameter(ArgumentCaptor.forClass(MultiPartEmail.class).capture(), smtpHost.capture(), smtpPort.capture(), ssl.capture(), user.capture(), password.capture(), debug.capture());

        assertThat(smtpHost.getValue(), is(smtpConfiguration.getHost()));
        assertThat(smtpConfiguration.getPort(), is(smtpConfiguration.getPort()));
        assertThat(smtpConfiguration.isSsl(), is(smtpConfiguration.isSsl()));
        assertThat(smtpConfiguration.getUsername(), is(smtpConfiguration.getUsername()));
        assertThat(smtpConfiguration.getPassword(), is(smtpConfiguration.getPassword()));
        assertThat(debug.getValue(), is(true));

        assertThat(mail.getFrom(), is(emailConfiguration.getFrom()));
        assertThat(mail.getBccs(), containsInAnyOrder(emailConfiguration.getBcc()));
        assertThat(mail.getReplyTo(), containsInAnyOrder(emailConfiguration.getReplyTo()));
    }
}
