package com.kwery.tests.services.job.schedule;

import com.google.common.collect.ImmutableMap;
import com.kwery.models.JobModel;
import com.kwery.services.job.ReportEmailSender;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportEmailSenderShouldSendTest {
    private ReportEmailSender reportEmailSender;

    @Before
    public void setUp() {
        reportEmailSender = new ReportEmailSender(null, null, null, null);
    }

    @Test
    public void testSend() {
        JobModel jobModel = new JobModel();

        jobModel.setRules(new HashMap<>());
        assertThat(reportEmailSender.shouldSend(true, jobModel), is(true));
        assertThat(reportEmailSender.shouldSend(false, jobModel), is(true));

        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(false)));
        assertThat(reportEmailSender.shouldSend(true, jobModel), is(true));
        assertThat(reportEmailSender.shouldSend(false, jobModel), is(true));

        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(true)));
        assertThat(reportEmailSender.shouldSend(false, jobModel), is(false));
    }

    @Test
    public void testDoNotSend() {
        JobModel jobModel = new JobModel();
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(true)));
        assertThat(reportEmailSender.shouldSend(true, jobModel), is(true));
    }
}
