package com.kwery.tests.services.job.email.withcontent;

import com.kwery.services.job.ReportEmailSender;
import org.junit.Test;

public class ReportEmailSenderEmptyReportEmailRuleFalseTest extends AbstractReportEmailWithContentSender {
    @Test
    public void test() throws Exception {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);
        assertMailPresent();
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
