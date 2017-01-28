package com.kwery.tests.fluentlenium.job.reportlist;

import org.junit.Before;
import org.junit.Test;

import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportListUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        super.setResultCount(1);
        super.setUp();
    }

    @Test
    public void test() throws InterruptedException {
        //page 0
        page.waitForFluentField("1");
        page.waitUntilPreviousIsDisabled();
        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(true));
        asserts();

        //page 1
        page.clickNext();
        page.waitUntilPreviousIsEnabled();
        page.waitForFluentField("1");
        assertThat(page.isPreviousEnabled(), is(true));
        assertThat(page.isNextEnabled(), is(true));
        asserts();

       //page 2
        page.clickNext();
        page.waitUntilNextIsDisabled();
        page.waitForFluentField("1");
        assertThat(page.isPreviousEnabled(), is(true));
        assertThat(page.isNextEnabled(), is(false));
        asserts();

        //page 1
        page.clickPrevious();
        page.waitUntilNextIsEnabled();
        page.waitForFluentField("1");
        assertThat(page.isPreviousEnabled(), is(true));
        assertThat(page.isNextEnabled(), is(true));
        asserts();

        //page 0
        page.clickPrevious();
        page.waitUntilPreviousIsDisabled();
        page.waitForFluentField("1");
        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(true));
        asserts();
    }

    private void asserts() {
        ReportListRow row = page.rows().get(0);
        ReportListRow expected = rowMap.get(row.getLabel());
        assertThat(row, theSameBeanAs(expected));
    }
}
