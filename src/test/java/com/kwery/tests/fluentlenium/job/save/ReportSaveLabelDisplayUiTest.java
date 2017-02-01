package com.kwery.tests.fluentlenium.job.save;

import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.job.save.add.AbstractReportSaveUiTest;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ReportSaveLabelDisplayUiTest extends AbstractReportSaveUiTest {
    private JobLabelModel jobLabelModel0;
    private JobLabelModel jobLabelModel1;

    @Before
    public void setUp() {
        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        super.setUp();
    }

    @Test
    public void testAllLabelsShown() {
        assertThat(page.labelTexts(0), containsInAnyOrder("", jobLabelModel0.getLabel(), jobLabelModel1.getLabel()));

        page.clickOnAddLabel(1);
        assertThat(page.labelTexts(0), containsInAnyOrder("", jobLabelModel0.getLabel(), jobLabelModel1.getLabel()));
        assertThat(page.labelTexts(1), containsInAnyOrder("", jobLabelModel0.getLabel(), jobLabelModel1.getLabel()));

        page.clickOnRemoveLabel(0);
        assertThat(page.labelTexts(0), containsInAnyOrder("", jobLabelModel0.getLabel(), jobLabelModel1.getLabel()));

        page.clickOnRemoveLabel(0);
    }
}
