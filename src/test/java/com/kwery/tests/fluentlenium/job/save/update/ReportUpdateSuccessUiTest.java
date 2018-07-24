package com.kwery.tests.fluentlenium.job.save.update;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ReportUpdateSuccessUiTest extends AbstractReportUpdateSuccessUiTest {
    @Parameterized.Parameter
    public boolean isCopy;

    @Parameterized.Parameters(name = "copy{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true},
                {false},
        });
    }

    @Override
    public boolean getCopy() {
        return isCopy;
    }
}
