package com.kwery.tests.fluentlenium.job;

import com.google.common.collect.ImmutableMap;
import com.kwery.models.JobModel;
import org.junit.Before;

import java.util.HashSet;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobModelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static junit.framework.TestCase.fail;

public class ReportSaveWithDependentsSuccessUiTest extends ReportSaveSuccessUiTest {
    protected JobModel jobModel;

    @Before
    public void ReportSaveWithDependentsSuccessUiTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(new HashSet<>());
        jobModelDbSetUp(jobModel);

        page = createPage(ReportSavePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report save page");
        }
    }

    public void test() {
        page.toggleParentReport();
        page.waitUntilParentReportIsEnabled();

        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );
        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        Map<Integer, String> parentReportIdToLabelMap = ImmutableMap.of(
                jobModel.getId(), jobModel.getLabel()
        );
        page.setParentJobIdToLabelMap(parentReportIdToLabelMap);

        jobDto.setCronExpression("");
        jobDto.setParentJobId(jobModel.getId());

        page.fillAndSubmitReportSaveForm(jobDto);
        page.waitForReportSaveSuccessMessage();
    }
}
