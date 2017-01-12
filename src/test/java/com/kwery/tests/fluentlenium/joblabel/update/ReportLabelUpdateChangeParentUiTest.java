package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.models.JobLabelModel;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportLabelUpdateChangeParentUiTest extends AbstractReportLabelUpdateUiTest {
    private JobLabelModel jobLabelModel;
    private JobLabelModel parentJobLabelModel1;

    @Before
    public void setUp() {
        JobLabelModel parentJobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel0);

        parentJobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel1);

        jobLabelModel = jobLabelModel();
        jobLabelModel.setParentLabel(parentJobLabelModel0);
        jobLabelDbSetUp(jobLabelModel);

        super.setUp();
    }

    @Test
    public void test() {
        int selected = page.selectedParentLabelOptionIndex();
        int updated = 2;
        //Only two parents
        if (selected == 2) {
            updated = 1;
        }
        page.parentLabel(updated);
        page.submitForm();
        page.waitForLabelSaveSuccessMessage(getReportLabel());

        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(3));
        assertThat(jobLabelDao.getJobLabelModelByLabel(getReportLabel()).getParentLabel().getLabel(), is(parentJobLabelModel1.getLabel()));
    }

    @Override
    public int getReportLabelId() {
        return jobLabelModel.getId();
    }

    @Override
    public String getReportLabel() {
        return jobLabelModel.getLabel();
    }
}
