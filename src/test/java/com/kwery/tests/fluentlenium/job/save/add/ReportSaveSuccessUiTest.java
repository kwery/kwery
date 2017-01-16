package com.kwery.tests.fluentlenium.job.save.add;

import com.kwery.tests.fluentlenium.job.save.JobForm;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.assertJobModel;

public class ReportSaveSuccessUiTest extends AbstractReportSaveUiTest {
    @Test
    public void testWithCronExpressionChosen() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }

    @Test
    public void testWithCronUiChosen() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        jobForm.setUseCronUi(true);

        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }

    @Test
    public void testWithEmailRuleChecked() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        jobDto.setEmptyReportNoEmailRule(true);
        JobForm jobForm = mapper.map(jobDto, JobForm.class);

        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }

    @Test
    public void testWithEmailRuleUnchecked() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        jobDto.setEmptyReportNoEmailRule(false);
        JobForm jobForm = mapper.map(jobDto, JobForm.class);

        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportSaveSuccessMessage();

        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }
}
