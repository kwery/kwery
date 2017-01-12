package com.kwery.tests.fluentlenium.joblabel.save;

import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class ReportLabelSaveWithParentSuccessUiTest extends AbstractReportLabelSaveTest {
    private JobLabelModel jobLabelModel0;
    private JobLabelModel jobLabelModel1;
    private Map<String, JobLabelModel> labelMap;

    @Before
    public void setUp() {
        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = TestUtil.jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        labelMap = ImmutableMap.of(
                jobLabelModel0.getLabel(), jobLabelModel0,
                jobLabelModel1.getLabel(), jobLabelModel1
        );

        super.setUp();

        jobLabelDao = ninjaServerRule.getInjector().getInstance(JobLabelDao.class);
    }

    @Test
    public void test() {
        String label = "test";
        page.fillAndSubmitForm(label, 1);
        page.waitForLabelSaveSuccessMessage(label);

        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(3));

        JobLabelModel childLabel = jobLabelDao.getJobLabelModelByLabel(label);
        labelMap.get(childLabel.getParentLabel().getLabel()).getChildLabels().add(childLabel);

        assertThat(childLabel.getParentLabel(), theSameBeanAs(labelMap.get(childLabel.getParentLabel().getLabel())));
    }
}
