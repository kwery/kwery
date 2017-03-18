package com.kwery.tests.fluentlenium.job.save.add;

import com.kwery.tests.fluentlenium.job.save.JobForm;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static com.kwery.tests.util.TestUtil.assertJobModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ReportSaveReportSubscriptionEmailAndReportFailureAlertEmailDisabledUiTest extends AbstractReportSaveUiTest {
    protected boolean onboardingFlow;

    public ReportSaveReportSubscriptionEmailAndReportFailureAlertEmailDisabledUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

    @Before
    public void setUp() {
        super.setOnboardingFlow(onboardingFlow);
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
