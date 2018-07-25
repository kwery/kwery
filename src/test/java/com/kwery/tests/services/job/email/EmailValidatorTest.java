package com.kwery.tests.services.job.email;

import com.kwery.services.mail.EmailValidator;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailValidatorTest {
    @Test
    public void testFilterInvalidEmails() {
        List<String> emails = new LinkedList<>();
        emails.add("");
        emails.add("abhyrama@gmail.com");
        emails.add("foobarmoo");

        EmailValidator emailValidator = new EmailValidator();
        List<String> invalids = emailValidator.filterInvalidEmails(emails);

        assertThat(invalids, containsInAnyOrder("", "foobarmoo"));
    }

    @Test
    public void testEmailCsvManipulation() {
        EmailValidator emailValidator = new EmailValidator();
        assertThat(emailValidator.emailCsvManipulation("abhyrama@gmail.com,,    purvi@getkwery.com  "), is("abhyrama@gmail.com,purvi@getkwery.com"));
        assertThat(emailValidator.emailCsvManipulation(""), is(""));
        assertThat(emailValidator.emailCsvManipulation("  "), is(""));
    }
}
