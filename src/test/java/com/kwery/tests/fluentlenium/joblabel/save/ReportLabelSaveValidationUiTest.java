package com.kwery.tests.fluentlenium.joblabel.save;

import com.kwery.models.JobLabelModel;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ReportLabelSaveValidationUiTest extends AbstractReportLabelSaveUiTest {
    @Before
    public void setUp() {
        JobLabelModel jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);
        super.setUp();
    }

    @Test
    public void testNameValidationError() {
        page.submitForm();
        page.waitForLabelNameValidationError();
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));

        String label = "test";
        page.fillName(label);
        page.waitForLabelNameValidationErrorRemoval();
        //Post validation cleanup, form can be submitted
        page.submitForm();
        page.waitForLabelSaveSuccessMessage(label);
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
    }

    @Test
    public void testValidationError() {
        page.optParentLabel();
        page.submitForm();
        page.waitForLabelNameValidationError();
        page.waitForParentLabelValidationError();
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));

        //Uncheck parent label
        page.optParentLabel();
        page.waitForParentLabelValidationErrorRemoval();

        //Post validation cleanup, form can be submitted
        String label = "test";
        page.fillAndSubmitForm(label, 1);
        page.waitForLabelSaveSuccessMessage(label);

        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
        assertThat(jobLabelDao.getJobLabelModelByLabel(label).getParentLabel(), notNullValue());
    }

    @Test
    public void uncheckParentFormSubmit() {
        String label = "test";
        page.fillName(label);
        page.optParentLabel();
        page.submitForm();
        page.waitForParentLabelValidationError();
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));
        page.optParentLabel();
        page.waitForParentLabelValidationErrorRemoval();
        page.submitForm();
        page.waitForLabelSaveSuccessMessage(label);
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
    }
}
