package com.kwery.tests.services.job.email.withcontent;

import com.kwery.services.job.ReportEmailSender;
import org.junit.Test;

public class ReportEmailSenderEmptyReportEmailRuleTrueTest extends AbstractReportEmailWithContentSender {
    @Test
    public void test() throws Exception {
        getInstance(ReportEmailSender.class).send(jobExecutionModel);
        assertMailPresent();
    }


    @Override
    public boolean getEmptyReportEmailRule() {
        return true;
    }
}
