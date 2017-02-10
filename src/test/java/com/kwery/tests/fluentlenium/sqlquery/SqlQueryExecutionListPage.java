package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.models.SqlQueryExecutionModel.Status;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class SqlQueryExecutionListPage extends KweryFluentPage implements RepoDashPage {
    public static final int RESULT_TABLE_COLUMN_COUNT = 3;

    protected int sqlQueryId;

    @Override
    public boolean isRendered() {
        waitForModalDisappearance();
        await().atMost(TestUtil.TIMEOUT_SECONDS, SECONDS).until($("#executionListTableBody")).displayed();
        return $("#executionListTableBody").first().displayed();
    }

    @Override
    public String getUrl() {
        return format("/#sql-query/%d/execution-list", getSqlQueryId());
    }

    public List<String> getExecutionListHeaders() {
        List<String> headers = new ArrayList<>(RESULT_TABLE_COLUMN_COUNT);
        List<FluentWebElement> headerColumns = $("#executionListTable thead th");
        for (FluentWebElement headerColumn : headerColumns) {
            headers.add(headerColumn.text());
        }

        return headers;
    }

    public List<List<String>> getExecutionList() {
        List<List<String>> resultRows = new LinkedList<>();

        List<FluentWebElement> rows = $("#executionListTableBody tr");

        for (FluentWebElement row : rows) {
            List<String> resultColumns = new ArrayList<>(RESULT_TABLE_COLUMN_COUNT);

            List<FluentWebElement> columns = row.find(By.tagName("td"));

            for (FluentWebElement column : columns) {
                resultColumns.add(column.text());
            }

            resultRows.add(resultColumns);
        }

        return resultRows;
    }

    public String sqlQuery() {
        return find(className("f-sql-query")).text();
    }

    public void fillStatus(Status... statuses) {
        for (Status status : statuses) {
            find(id(status.name().toLowerCase() + "CheckBox")).click();
        }
    }

    public void clickFilter() {
        $(className("f-collapse")).click();
        await().atMost(30, SECONDS).until($("#collapseOne")).displayed();
    }

    public boolean isFilterCollapsed() {
        return !$(id("collapseOne")).first().displayed();
    }

    public boolean isFilterOpen() {
        return !isFilterCollapsed();
    }

    public void fillExecutionStartStart(String date) {
        $("#executionStartStart").fill().with(date);
    }

    public void fillExecutionStartEnd(String date) {
        $("#executionStartEnd").fill().with(date);
    }

    public void fillExecutionEndStart(String date) {
        $("#executionEndStart").fill().with(date);
    }

    public void fillExecutionEndEnd(String date) {
        $("#executionEndEnd").fill().with(date);
    }

    public void fillResultCount(int resultCount) {
       $("#resultCount").fill().with(String.valueOf(resultCount));
    }

    public void clickPrevious() {
        find(id("previous")).click();
    }

    public void clickNext() {
        find(id("next")).click();
    }

    public void filter() {
        find(id("filterButton")).click();
    }

    public void waitForFilterResult(int expectedRowCount) {
        await().atMost(30, SECONDS).until($("#executionListTableBody tr")).size(expectedRowCount);
    }

    public void waitForStatus(Status status) {
        await().atMost(30, SECONDS).until($(".status")).text(status.name());
    }

    public boolean isNextEnabled() {
        return !Arrays.asList(find(className("f-next")).attribute("class").split(" ")).contains("disabled");
    }

    public boolean isPreviousEnabled() {
        return !Arrays.asList(find(className("f-previous")).attribute("class").split(" ")).contains("disabled");
    }

    public String statusLink(int position) {
        return find(className("status-link")).get(position).attribute("href");
    }

    public boolean isStatusText(int position) {
        return find(className("status")).get(position).find(By.tagName("span")).first().displayed();
    }

    public boolean isStatusLink(int position) {
        return find(className("status")).get(position).find(By.tagName("a")).first().displayed();
    }

    public int getSqlQueryId() {
        return sqlQueryId;
    }

    public void setSqlQueryId(int sqlQueryId) {
        this.sqlQueryId = sqlQueryId;
    }
}
