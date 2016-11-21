package com.kwery.tests.models;

import com.kwery.models.SmtpDetail;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class SmtpDetailPasswordObfuscationTest {
    @Test
    public void test() {
        SmtpDetail smtpDetail = new SmtpDetail();
        String password = UUID.randomUUID().toString();
        smtpDetail.setPassword(password);
        assertThat(smtpDetail.toString(), not(containsString(smtpDetail.getPassword())));
    }
}
