package com.kwery.tests.fluentlenium.job.save.add;

import com.kwery.tests.fluentlenium.job.save.JobForm;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.tests.util.TestUtil.assertJobModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportSaveReportSubscriptionEmailAndReportFailureAlertEmailDisabledUiTest extends AbstractReportSaveUiTest {
    @Before
    public void setUp() {
        this.setSmtpConfigurationSave(false);
        this.setUrlConfigurationSave(false);
        super.setUp();
    }

    @Test
    public void test() {
        assertThat(page.isEmailFieldEnabled(), is(false));
        assertThat(page.isFailureAlertEmailFieldEnabled(), is(false));
        assertThat(page.isEmptyReportNoEmailRuleEnabled(), is(false));

        jobDto.setEmails(new HashSet<>());
        jobDto.setJobFailureAlertEmails(new HashSet<>());
        jobDto.setEmptyReportNoEmailRule(false);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }
}
