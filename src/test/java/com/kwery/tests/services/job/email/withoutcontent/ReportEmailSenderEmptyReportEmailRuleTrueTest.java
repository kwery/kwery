package com.kwery.tests.services.job.email.withoutcontent;

import com.kwery.services.job.ReportEmailSender;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ReportEmailSenderEmptyReportEmailRuleTrueTest extends AbstractReportEmailWithoutContentSender {
    @Test
    public void test() {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
        }

        assertThat(wiserRule.wiser().getMessages().isEmpty(), is(true));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return true;
    }
}
