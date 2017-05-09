package com.kwery.tests.services.job.email.reporteamailcreator.withoutcontent;

import com.kwery.services.job.ReportEmailCreator;
import com.kwery.services.mail.KweryMail;
import org.junit.Test;

import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

public class ReportEmailSenderEmptyReportEmailRuleTrueTest extends AbstractReportEmailWithoutContentSender {
    @Test
    public void test() {
        KweryMail kweryMail = getInstance(ReportEmailCreator.class).create(jobExecutionModel, new LinkedList<>());
        assertThat(kweryMail, nullValue());
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return true;
    }
}
