package com.kwery.tests.services.job.email.reporteamailcreator.withcontent;

import com.kwery.services.job.ReportEmailCreator;
import com.kwery.services.mail.KweryMail;
import org.junit.Test;

import java.util.LinkedList;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ReportEmailSenderEmptyReportEmailRuleFalseTest extends AbstractReportEmailCreatorWithContentTest {
    @Test
    public void test() throws Exception {
        KweryMail kweryMail = getInstance(ReportEmailCreator.class).create(jobExecutionModel, new LinkedList<>());
        assertThat(kweryMail, notNullValue());
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
