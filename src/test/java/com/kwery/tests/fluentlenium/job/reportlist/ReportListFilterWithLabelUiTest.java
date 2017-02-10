package com.kwery.tests.fluentlenium.job.reportlist;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportListFilterWithLabelUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        super.setResultCount(1);
        super.setUp();
    }

    @Test
    public void testWithLabelSelected() throws Exception {
        //page 0
        page.waitForRows(1);
        page.waitForFluentField("1");
        SECONDS.sleep(5);
        asserts();
        page.selectLabel(1);

        //page 0
        page.filterReport();
        page.waitForFluentField("1");
        page.waitForRows(1);
        SECONDS.sleep(5);
        asserts();
        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(true));
        page.clickNext();

        //page 1
        page.waitUntilPreviousIsEnabled();
        page.waitForFluentField("1");
        SECONDS.sleep(5);
        assertThat(page.isPreviousEnabled(), is(true));
        assertThat(page.isNextEnabled(), is(false));
        asserts();

        page.clickPrevious();
        page.waitForFluentField("1");
        page.waitForRows(1);
        SECONDS.sleep(5);
        asserts();
        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(true));
    }

    private void asserts() {
        List<ReportListRow> rows = page.rows();
        for (ReportListRow row : rows) {
            ReportListRow expected = rowMap.get(row.getLabel());
            assertThat(row, theSameBeanAs(expected));
        }
        assertThat(page.labelTexts(), containsInAnyOrder(jobLabelModel.getLabel(), ""));
    }
}
