package com.kwery.tests.fluentlenium.joblabel;

import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportLabelSaveParentReportPickUiTest extends AbstractReportLabelSaveTest {
    public static final int LABELS = 3;

    @Before
    public void setUp() {
        JobLabelModel parentJobLabelModel = null;
        for (int i = 0; i < LABELS; ++i) {
            JobLabelModel jobLabelModel = TestUtil.jobLabelModel();

            if (parentJobLabelModel != null) {
                jobLabelModel.setParentLabel(parentJobLabelModel);
            }

            DbUtil.jobLabelDbSetUp(jobLabelModel);

            parentJobLabelModel = jobLabelModel;
        }

        super.setUp();
    }

    @Test
    public void test() {
        int parentLabelIndex = RandomUtils.nextInt(1, LABELS);
        String label = UUID.randomUUID().toString();
        page.fillAndSubmitForm(label, parentLabelIndex);
        page.waitForLabelSaveSuccessMessage(label);
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(LABELS + 1));
        assertThat(jobLabelDao.getJobLabelModelByLabel(label).getParentLabel().getLabel(), is(page.parentLabelText(parentLabelIndex).trim()));
    }
}
