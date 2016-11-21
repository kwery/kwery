package com.kwery.tests.models;

import com.kwery.models.SmtpDetails;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class SmtpDetailsPasswordObfuscationTest {
    @Test
    public void test() {
        SmtpDetails smtpDetails = new SmtpDetails();
        String password = UUID.randomUUID().toString();
        smtpDetails.setPassword(password);
        assertThat(smtpDetails.toString(), not(containsString(smtpDetails.getPassword())));
    }
}
