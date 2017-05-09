package com.kwery.tests.services.job.email.reporteamailcreator.sqlqueryemailsetting;

import com.kwery.services.mail.KweryMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static com.kwery.tests.services.job.email.EmailHtmlTestUtil.assertReportFooter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(Parameterized.class)
public class SqlQueryEmailSettingTest extends AbstractSqlQueryEmailSettingTest {
    protected boolean includeInBody;
    protected boolean includeInAttachment;

    public SqlQueryEmailSettingTest(boolean includeInBody, boolean includeInAttachment) {
        this.includeInBody = includeInBody;
        this.includeInAttachment = includeInAttachment;
    }

    @Parameterized.Parameters(name = "IncludeInBody-{0}-IncludeInAttachment-{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true, false},
                {true, true},
                {false, false},
                {false, true},
        });
    }

    @Test
    public void testAllFalse() throws Exception {
        setEmailSetting(includeInBody, includeInAttachment);
        KweryMail kweryMail = reportEmailCreator.create(jobExecutionModel, new LinkedList<>());

        String htmlContent = kweryMail.getBodyHtml();
        assertReportFooter(htmlContent);

        assertSection(htmlContent, includeInBody);
        assertThat(kweryMail.getAttachments().isEmpty(), is(!includeInAttachment));
    }
}
