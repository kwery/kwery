package com.kwery.tests.fluentlenium.job.save.add;

import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.job.save.JobForm;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ReportSaveWithLabelSuccessUiTest extends AbstractReportSaveUiTest {
    private JobLabelModel jobLabelModel0;
    private JobLabelModel jobLabelModel1;

    @Before
    public void setUp() {
        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        JobLabelModel jobLabelModel2 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel2);

        super.setUp();
    }

    @Test
    public void test() {
        jobDto.getLabelIds().addAll(ImmutableSet.of(jobLabelModel0.getId(), jobLabelModel1.getId()));

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.waitForModalDisappearance();
        page.fillAndSubmitReportSaveForm(jobForm);
        page.waitForReportListPage();
        page.waitForReportSaveSuccessMessage();

        JobModel savedJob = jobDao.getJobByName(jobDto.getName());
        List<Integer> labelIds = savedJob.getLabels().stream().map(JobLabelModel::getId).collect(Collectors.toList());

        assertThat(labelIds, containsInAnyOrder(jobLabelModel0.getId(), jobLabelModel1.getId()));
    }
}
