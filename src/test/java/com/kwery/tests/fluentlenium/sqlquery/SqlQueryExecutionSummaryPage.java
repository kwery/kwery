package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.tagName;

public class SqlQueryExecutionSummaryPage extends FluentPage implements RepoDashPage {
    public static final int COLUMN_COUNT = 3;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-execution-summary-table")).displayed();
        return true;
    }

    @Override
    public String getUrl() {
        return "/#sql-query/execution-summary";
    }

    public List<String> getExecutionSummaryHeaders() {
        List<String> headers = new ArrayList<>(COLUMN_COUNT);
        List<FluentWebElement> headerColumns = $(".f-execution-summary-table thead th");
        for (FluentWebElement headerColumn : headerColumns) {
            headers.add(headerColumn.text());
        }

        return headers;
    }

    public List<List<String>> getExecutionSummary() {
        List<List<String>> resultRows = new LinkedList<>();

        List<FluentWebElement> rows = $(".f-execution-summary-table-body tr");

        for (FluentWebElement row : rows) {
            List<String> resultColumns = new ArrayList<>(COLUMN_COUNT);

            List<FluentWebElement> columns = row.find(tagName("td"));

            for (FluentWebElement column : columns) {
                resultColumns.add(column.text());
            }

            resultRows.add(resultColumns);
        }

        return resultRows;
    }

    public List<String> getReportLinks() {
        List<String> hrefs = new LinkedList<>();

        List<FluentWebElement> rows = $(".f-execution-summary-table-body tr");

        for (FluentWebElement row : rows) {
            List<FluentWebElement> columns = row.find(tagName("td"));
            String href = columns.get(columns.size() - 1).find(tagName("a")).first().attribute("href");
            hrefs.add(href);
        }

        return hrefs;
    }
}
