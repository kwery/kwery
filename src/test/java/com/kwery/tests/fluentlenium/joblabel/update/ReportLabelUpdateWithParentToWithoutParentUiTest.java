package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.models.JobLabelModel;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ReportLabelUpdateWithParentToWithoutParentUiTest extends AbstractReportLabelUpdateUiTest {
    private JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        JobLabelModel parentJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel);

        jobLabelModel = jobLabelModel();
        jobLabelModel.setParentLabel(parentJobLabelModel);
        jobLabelDbSetUp(jobLabelModel);

        super.setUp();
    }

    @Test
    public void test() {
        page.optParentLabel();
        page.submitForm();
        page.waitForLabelSaveSuccessMessage(getReportLabel());
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
        assertThat(jobLabelDao.getJobLabelModelByLabel(getReportLabel()).getParentLabel(), nullValue());
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
