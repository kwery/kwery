package com.kwery.tests.fluentlenium.job.executing;

import com.kwery.dtos.JobExecutionDto;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import java.util.HashMap;
import java.util.Map;

import static com.kwery.tests.fluentlenium.job.executing.ReportExecutingPage.ExecutingReports.name;
import static com.kwery.tests.fluentlenium.job.executing.ReportExecutingPage.ExecutingReports.start;
import static com.kwery.tests.util.Messages.REPORT_JOB_EXECUTING_STOP_FAILURE_M;
import static com.kwery.tests.util.Messages.REPORT_JOB_EXECUTING_STOP_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/executing")
public class ReportExecutingPage extends KweryFluentPage implements RepoDashPage {
    protected ActionResultComponent actionResultComponent;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".executing-reports-f")).displayed();
        return true;
    }

    public void stopExecution(int row) {
        el("div", withClass().contains(String.format("executing-reports-list-%d-f", row))).el(".stop-execution-f").click();
    }

    public void assertStopExecutionFailureMessage() {
        actionResultComponent.assertFailureMessage(REPORT_JOB_EXECUTING_STOP_FAILURE_M);
    }

    public void assertStopExecutionSuccessMessage() {
        actionResultComponent.assertSuccessMessage(REPORT_JOB_EXECUTING_STOP_SUCCESS_M);
    }

    public void assertExecutingReports(int row, Map<ExecutingReports, ?> map) {
        assertThat(el(String.format(".executing-reports-list-%d-f .name-f", row), withText(String.valueOf(map.get(name))))).isDisplayed();
        assertThat(el(String.format(".executing-reports-list-%d-f .start-f", row), withText(String.valueOf(map.get(start))))).isDisplayed();
    }

    public Map<ExecutingReports, ?> toMap(JobExecutionDto dto) {
        Map map = new HashMap();
        map.put(name, dto.getLabel());
        map.put(start, dto.getStart());
        return map;
    }

    public enum ExecutingReports {
        name, start
    }
}
