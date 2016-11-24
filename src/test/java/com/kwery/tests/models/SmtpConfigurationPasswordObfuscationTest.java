package com.kwery.tests.models;

import com.kwery.models.SmtpConfiguration;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class SmtpConfigurationPasswordObfuscationTest {
    @Test
    public void test() {
        SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
        String password = UUID.randomUUID().toString();
        smtpConfiguration.setPassword(password);
        assertThat(smtpConfiguration.toString(), not(containsString(smtpConfiguration.getPassword())));
    }
}
