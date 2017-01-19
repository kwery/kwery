package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.models.JobLabelModel;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ReportLabelUpdateWithoutParentUiTest extends AbstractReportLabelUpdateUiTest {
    private JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);
        super.setUp();
    }

    @Test
    public void test() {
        page.waitForModalDisappearance();
        String newLabel = UUID.randomUUID().toString();
        page.fillAndSubmitForm(newLabel, null);
        page.waitForJobLabelListPage();
        page.waitForLabelSaveSuccessMessage(newLabel);
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));
        JobLabelModel fromDb = jobLabelDao.getJobLabelModelByLabel(newLabel);
        assertThat(fromDb, notNullValue());
        assertThat(fromDb.getParentLabel(), nullValue());
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
