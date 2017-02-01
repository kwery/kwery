package com.kwery.tests.fluentlenium.job;

import com.kwery.dtos.JobExecutionDto;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;

import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.Messages.REPORT_JOB_EXECUTING_STOP_FAILURE_M;
import static com.kwery.tests.util.Messages.REPORT_JOB_EXECUTING_STOP_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report/executing")
public class ReportExecutingPage extends KweryFluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".executing-reports-f")).displayed();
        return true;
    }

    public List<JobExecutionDto> executions() {
        List<JobExecutionDto> dtos = new LinkedList<>();

        for (FluentWebElement tr : $(".executing-reports-tbody-f tr")) {
            List<FluentWebElement> tds = tr.find(tagName("td"));

            JobExecutionDto dto = new JobExecutionDto();

            dto.setLabel(tds.get(0).text());
            dto.setStart(tds.get(1).text());

            dtos.add(dto);
        }

        return dtos;
    }

    public void waitForExecutingReportsList(int expectedRowCount) {
        await().atMost(30, SECONDS).until($(".executing-reports-tbody-f  tr")).size(expectedRowCount);
    }

    public void stopExecution(int row) {
        FluentList<FluentWebElement> fluentWebElements = find(".executing-reports-tbody-f tr");
        FluentWebElement tr = fluentWebElements.get(row);
        tr.find(className("stop-execution-f")).click();
    }

    public void waitForStopExecutionSuccessMessage() {
        super.waitForSuccessMessage(REPORT_JOB_EXECUTING_STOP_SUCCESS_M);
    }

    public void waitForStopExecutionFailureMessage() {
        super.waitForFailureMessage(REPORT_JOB_EXECUTING_STOP_FAILURE_M);
    }
}
