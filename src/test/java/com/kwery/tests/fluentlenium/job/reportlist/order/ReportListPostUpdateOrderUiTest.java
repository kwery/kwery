package com.kwery.tests.fluentlenium.job.reportlist.order;

import com.kwery.tests.fluentlenium.job.save.update.ReportUpdateSuccessUiTest;
import org.junit.Test;

import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class ReportListPostUpdateOrderUiTest extends ReportUpdateSuccessUiTest {
    @Test
    public void test() {
        super.test();
        assertThat(el(String.format(".report-list-%d-f .title-f", 0), withText(jobModel.getTitle()))).isDisplayed();
    }
}
