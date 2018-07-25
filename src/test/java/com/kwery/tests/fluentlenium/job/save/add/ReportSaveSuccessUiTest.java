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

import static com.kwery.tests.util.TestUtil.assertJobModel;

@RunWith(Parameterized.class)
public class ReportSaveSuccessUiTest extends AbstractReportSaveUiTest {
    protected boolean onboardingFlow;

    public ReportSaveSuccessUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true},
                {false},
        });
    }

    @Before
    public void setUp() {
        super.setOnboardingFlow(onboardingFlow);
        super.setUp();
    }

    @Test
    public void testWithCronExpressionChosen() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }

    @Test
    public void testWithCronUiChosen() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        jobForm.setUseCronUi(true);

        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }

    @Test
    public void testWithEmailRuleChecked() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        jobDto.setEmptyReportNoEmailRule(true);
        JobForm jobForm = mapper.map(jobDto, JobForm.class);

        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }

    @Test
    public void testWithEmailRuleUnchecked() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        jobDto.setEmptyReportNoEmailRule(false);
        JobForm jobForm = mapper.map(jobDto, JobForm.class);

        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }
}
