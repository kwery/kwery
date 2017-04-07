package com.kwery.tests.fluentlenium.job.save.add;

import com.kwery.tests.fluentlenium.job.save.JobForm;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.assertJobModel;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;

public class ReportSaveAndGenerateUiTest extends AbstractReportSaveUiTest {
    @Test
    public void test() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.waitForModalDisappearance();
        page.fillReportSaveForm(jobForm);
        page.submitReportGenerateForm();
        assertThat(el("div", withClass("report-section-f"))).isDisplayed();
        assertJobModel(jobDao.getJobByName(jobDto.getName()), null, jobDto, datasource);
    }
}
