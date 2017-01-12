package com.kwery.tests.fluentlenium.joblabel.save;

import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.TestUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportLabelSaveLabelWithParentPickParentUiTest extends AbstractReportLabelSaveUiTest {
    public static final int LABELS = 3;

    @Before
    public void setUp() {
        JobLabelModel parentJobLabelModel = null;
        for (int i = 0; i < LABELS; ++i) {
            JobLabelModel jobLabelModel = TestUtil.jobLabelModel();

            if (parentJobLabelModel != null) {
                jobLabelModel.setParentLabel(parentJobLabelModel);
            }

            jobLabelDbSetUp(jobLabelModel);

            parentJobLabelModel = jobLabelModel;
        }

        super.setUp();
    }

    @Test
    public void testLabelSavedWithSelectedParent() {
        int parentLabelIndex = RandomUtils.nextInt(1, LABELS);
        String label = UUID.randomUUID().toString();
        page.fillAndSubmitForm(label, parentLabelIndex);
        page.waitForLabelSaveSuccessMessage(label);
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(LABELS + 1));
        assertThat(jobLabelDao.getJobLabelModelByLabel(label).getParentLabel().getLabel(), is(page.parentLabelText(parentLabelIndex).trim()));
    }

    @Test
    public void testAllLabelsPresentInParentDropDown() {
        List<String> expectedParentLabels = jobLabelDao.getAllJobLabelModels().stream().map(JobLabelModel::getLabel).collect(Collectors.toList());
        expectedParentLabels.add("");

        List<String> fromPage = page.parentLabelTexts().stream().map(String::trim).collect(Collectors.toList());

        assertThat(fromPage, containsInAnyOrder(fromPage.toArray(new String[fromPage.size()])));
    }
}
