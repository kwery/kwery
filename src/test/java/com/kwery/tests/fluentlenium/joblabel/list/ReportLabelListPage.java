package com.kwery.tests.fluentlenium.joblabel.list;

import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwery.tests.fluentlenium.joblabel.list.ReportLabelListPage.ReportLabelList.editLink;
import static com.kwery.tests.fluentlenium.joblabel.list.ReportLabelListPage.ReportLabelList.label;
import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report-label/list")
public class ReportLabelListPage extends KweryFluentPage implements RepoDashPage {
    protected ActionResultComponent actionResultComponent;

    @Override
    public boolean isRendered() {
        return true;
    }

    public List<String> getLabels() {
        return $(className("label-f")).stream().map(FluentWebElement::text).collect(toList());
    }

    public void clickDelete(int row) {
        el("div", withClass().contains(String.format("report-label-list-%d-f", row))).el("button.delete-f").withHook(WaitHook.class).click();
    }

    public void assertFailureMessages(List<String> messages) {
        actionResultComponent.assertFailureMessages(messages);
    }

    public void waitForSuccessMessage(String label) {
        super.waitForSuccessMessage(MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_SUCCESS_M, label));
    }

    public void assertSuccessMessage(String label) {
        actionResultComponent.assertSuccessMessage(MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_SUCCESS_M, label));
    }

    public void assertRow(int row, Map<ReportLabelList, ?> map) {
        assertThat(el(String.format(".report-label-list-%d-f .label-f", row), withText().contains(String.valueOf(map.get(label))))).isDisplayed();
        assertThat(el(String.format(".report-label-list-%d-f .edit-f", row), with("href").contains(String.valueOf(map.get(editLink)))))
                .isDisplayed();
    }

    public Map<ReportLabelList, ?> toMap(JobLabelModel model) {
        Map map = new HashMap();
        map.put(label, model.getLabel());
        map.put(editLink, String.format("/#report-label/%d", model.getId()));
        return map;
    }

    public enum ReportLabelList {
        label, editLink
    }
}
