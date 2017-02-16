package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;
import org.openqa.selenium.By;

import java.util.*;
import java.util.stream.Collectors;

import static com.kwery.tests.fluentlenium.datasource.DatasourceListPage.DatasourceList.*;
import static com.kwery.tests.util.Messages.DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M;
import static com.kwery.tests.util.Messages.DATASOURCE_DELETE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.openqa.selenium.By.id;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#datasource/list")
public class DatasourceListPage extends KweryFluentPage implements RepoDashPage {
    protected ActionResultComponent actionResultComponent;

    public static final int COLUMNS = 5;

    @Override
    public boolean isRendered() {
        return find(id("datasourcesListTable")).first().displayed();
    }

    public List<String> headers() {
        List<String> headers = new ArrayList<>(COLUMNS);

        for (FluentWebElement header : $("#datasourcesListTable tr th")) {
            headers.add(header.text());
        }

        return headers;
    }

    public List<List<String>> rows() {
        List<List<String>> rows = new LinkedList<>();

        for (FluentWebElement tr : $("#datasourcesListTableBody tr")) {
            List<String> row = new ArrayList<>(COLUMNS);
            row.addAll(tr.find(By.tagName("td")).stream().map(FluentWebElement::text).collect(Collectors.toList()));
            rows.add(row);
        }

        return rows;
    }

    public void waitForRows(int rowCount) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($("#datasourcesListTableBody tr")).size(rowCount);
    }

    public void delete(int row) {
        el("div", withClass().contains(String.format("datasource-list-%d-f", row))).el("button.delete-f").withHook(WaitHook.class).click();
    }

    public void assertDeleteSuccessMessage(String label) {
        actionResultComponent.assertSuccessMessage(format(DATASOURCE_DELETE_SUCCESS_M, label));
    }

    public void assertDeleteFailureMessage(String label) {
        actionResultComponent.assertFailureMessage(format(DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M, label));
    }

    public void assertDatasourceList(int row, Map<DatasourceList, ?> map) {
        assertThat(el(String.format(".datasource-list-%d-f .host-f", row), withText(String.valueOf(map.get(host))))).isDisplayed();
        assertThat(el(String.format(".datasource-list-%d-f .type-f", row), withText(String.valueOf(map.get(type))))).isDisplayed();
        assertThat(el(String.format(".datasource-list-%d-f .label-f", row), withText(String.valueOf(map.get(label))))).isDisplayed();
        assertThat(el(String.format(".datasource-list-%d-f .edit-f", row), with("href").contains(String.valueOf(map.get(editAction)))))
                .isDisplayed();
    }

    public enum DatasourceList {
        host, type, label, editAction
    }

    public Map<DatasourceListPage.DatasourceList, ?> toMap(Datasource datasource) {
        Map map = new HashMap();

        map.put(host, datasource.getUrl());
        map.put(type, datasource.getType());
        map.put(label, datasource.getLabel());
        map.put(editAction, String.format("/#datasource/%d", datasource.getId()));

        return map;
    }
}
