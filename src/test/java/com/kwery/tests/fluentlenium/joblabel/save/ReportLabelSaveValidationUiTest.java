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
        page.waitForModalDisappearance();
        page.submitForm();
        page.assertNonEmptyLabelNameValidationError();
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));

        String label = "test";
        page.fillName(label);
        page.waitForLabelNameValidationErrorRemoval();
        //Post validation cleanup, form can be submitted
        page.submitForm();
        page.waitForJobLabelListPage();
        page.waitForLabelSaveSuccessMessage(label);
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
    }

    @Test
    public void testValidationError() {
        page.waitForModalDisappearance();
        page.optParentLabel();
        page.submitForm();
        page.assertNonEmptyLabelNameValidationError();
        page.assertNonEmptyParentLabelValidationError();
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));

        //Uncheck parent label
        page.optParentLabel();
        page.waitForParentLabelValidationErrorRemoval();

        //Post validation cleanup, form can be submitted
        String label = "test";
        page.fillAndSubmitForm(label, 1);
        page.waitForJobLabelListPage();
        page.waitForLabelSaveSuccessMessage(label);

        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
        assertThat(jobLabelDao.getJobLabelModelByLabel(label).getParentLabel(), notNullValue());
    }

    @Test
    public void uncheckParentFormSubmit() {
        page.waitForModalDisappearance();

        String label = "test";
        page.fillName(label);
        page.optParentLabel();
        page.submitForm();
        page.assertNonEmptyParentLabelValidationError();
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));
        page.optParentLabel();
        page.waitForParentLabelValidationErrorRemoval();
        page.submitForm();
        page.waitForJobLabelListPage();
        page.waitForLabelSaveSuccessMessage(label);
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(2));
    }
}
