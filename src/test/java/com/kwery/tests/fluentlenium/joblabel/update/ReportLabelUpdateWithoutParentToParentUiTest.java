package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.models.JobLabelModel;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportLabelUpdateWithoutParentToParentUiTest extends AbstractReportLabelUpdateUiTest {
    private JobLabelModel jobLabelModel;
    private JobLabelModel parentJobLabelModel;

    @Before
    public void setUp() {
        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);

        parentJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel);

        super.setUp();
    }

    @Test
    public void test() {
        page.parentLabel(1);
        page.optParentLabel();
        page.submitForm();
        page.waitForLabelSaveSuccessMessage(getReportLabel());
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
        assertThat(jobLabelDao.getJobLabelModelByLabel(getReportLabel()).getParentLabel().getLabel(), is(parentJobLabelModel.getLabel()));
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
