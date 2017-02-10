package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class ReportExecutionListFilterValidationUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(10);
        super.setUp();
    }

    @Test
    public void test() throws InterruptedException {
        page.waitForRows(4);
        String start = "Sat Jan 07 2017 22:00";
        String end = "Sat Jan 07 2017 21:10";
        page.fillStart(start);
        page.fillEnd(end);
        page.waitForEndValidationError();
        page.clickFilter();
        page.waitForStartValidationError();

        assertThat(page.executionListTable(), hasSize(4));

        page.filterResult("", "");
        assertThat(page.executionListTable(), hasSize(4)); // Empty date columns does not submit the page

        //Opposite sequence of above
        page.fillEnd(end);
        page.fillStart(start);
        page.waitForStartValidationError();
        page.removeCalendarDropDown();
        //The calendar dropdown covers the filter button and selenium is not able to click on it, hence removing the calendar dropdown above.
        page.clickFilter();
        page.waitForEndValidationError();
        page.fillStart("Sat Jan 07 2017 20:10");
        //Fixing validation clears both fields
        page.waitForStartValidationErrorRemoval();
        page.waitForEndValidationErrorRemoval();

        page.removeCalendarDropDown();

        //Fixing validation results in correct result
        page.filterResult("Sat Jan 07 2017 04:00", "Sat Jan 07 2017 05:45");
        page.waitForModalDisappearance();
        page.waitForRows(2);
    }
}
