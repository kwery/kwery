package com.kwery.tests.fluentlenium.job.save;

import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.models.JobRuleModel;
import com.kwery.tests.fluentlenium.job.save.add.AbstractReportSaveUiTest;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportSaveSequentialSqlQueryExecutionUiTest extends AbstractReportSaveUiTest {
    @Test
    public void test() {
        assertThat(page.isStopExecutionOnSqlQueryDisplayed(), is(false));
        page.setSequentialSqlQueryExecution(true);
        page.awaitUntilIsStopExecutionOnSqlQueryDisplayed();
        assertThat(page.isStopExecutionOnSqlQueryDisplayed(), is(true));

        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );

        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobForm jobForm = mapper.map(jobDto, JobForm.class);
        page.fillReportSaveForm(jobForm);

        page.setSequentialSqlQueryExecution(false);
        page.awaitUntilIsStopExecutionOnSqlQueryNotDisplayed();

        page.submitReportSaveForm();
        page.waitForReportSaveSuccessMessage();

        for (JobModel jobModel : ninjaServerRule.getInjector().getInstance(JobDao.class).getAllJobs()) {
            if (!jobModel.getId().equals(parentJobModel.getId())) {
                JobRuleModel jobRuleModel = jobModel.getJobRuleModel();
                assertThat(jobRuleModel.isSequentialSqlQueryExecution(), is(false));
                assertThat(jobRuleModel.isStopExecutionOnSqlQueryFailure(), is(false));
            }
        }

    }
}
