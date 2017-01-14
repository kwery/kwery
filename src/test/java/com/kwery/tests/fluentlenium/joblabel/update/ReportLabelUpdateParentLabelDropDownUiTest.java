package com.kwery.tests.fluentlenium.joblabel.update;

import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportLabelUpdateParentLabelDropDownUiTest extends AbstractReportLabelUpdateUiTest {
    public static final int LABELS = 3;

    JobLabelModel jobLabelModel;
    JobLabelModel displayedParentJobLabelModel;

    @Before
    public void setUp() {
        JobLabelModel parentJobLabelModel = null;
        for (int i = 0; i < LABELS; ++i) {
            JobLabelModel jobLabelModel = TestUtil.jobLabelModel();

            //One without any children will be displayed in the parent dropdown list
            if (parentJobLabelModel != null) {
                jobLabelModel.setParentLabel(parentJobLabelModel);
            } else {
                displayedParentJobLabelModel = jobLabelModel;
            }

            //Choose the one at the root of the label model
            if (i == 1) {
                this.jobLabelModel = jobLabelModel;
            }

            jobLabelDbSetUp(jobLabelModel);

            parentJobLabelModel = jobLabelModel;
        }

        super.setUp();
    }

    @Test
    public void test() {
        assertThat(page.parentLabelTexts(), hasSize(2));//Label to be updated is not part of the drop-down
        assertThat(page.parentLabelTexts().get(1), is(displayedParentJobLabelModel.getLabel()));
        assertThat(page.selectedParentLabelOptionText(), is(jobLabelModel.getParentLabel().getLabel()));
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
