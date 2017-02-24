package com.kwery.tests.fluentlenium.joblabel.list;

import org.junit.Test;

public class ReportLabelListUiTest extends AbstractReportLabelListUiTest {
    @Test
    public void test() {
        page.assertRow(0, page.toMap(parentJobLabelModel));
        page.assertRow(1, page.toMap(jobLabelModel));
    }
}
