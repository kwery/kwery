package com.kwery.tests.fluentlenium.job.reportlist.search;

import com.kwery.tests.fluentlenium.job.reportlist.AbstractReportListUiTest;
import org.junit.Test;

public class ReportListSearchInvalidCharactersUiTest extends AbstractReportListUiTest {
    @Test
    public void test() {
        page.search("@#$$^&*()))_)++#%^#^&^#@*&@$^&*");
        page.waitForModalDisappearance();
        page.assertInvalidSearchCharacters();
    }
}