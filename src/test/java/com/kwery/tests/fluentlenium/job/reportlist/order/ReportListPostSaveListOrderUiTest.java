package com.kwery.tests.fluentlenium.job.reportlist.order;

import com.kwery.tests.fluentlenium.job.save.JobForm;
import com.kwery.tests.fluentlenium.job.save.add.AbstractReportSaveUiTest;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class ReportListPostSaveListOrderUiTest extends AbstractReportSaveUiTest {
    @Test
    public void testWithCronExpressionChosen() throws InterruptedException {
        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();
        assertThat(el(String.format(".report-list-%d-f .title-f", 0), withText(jobDto.getTitle()))).isDisplayed();
    }
}
