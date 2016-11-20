package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static com.kwery.tests.util.Messages.ONE_OFF_EXECUTION_SUCCESS_MESSAGE_M;
import static com.kwery.tests.util.Messages.SQL_QUERY_DELETE_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.tagName;

public class ListSqlQueriesPage extends FluentPage implements RepoDashPage {
    public static final int LIST_SQL_QUERIES_COLUMNS = 4;

    @Override
    public boolean isRendered() {
        return find(id("sqlQueriesListTable")).first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/list";
    }

    public List<List<String>> rows() {
        List<List<String>> container = new LinkedList<>();

        for (FluentWebElement fluentWebElement : $("#sqlQueriesListTableBody tr")) {
            List<String> row = new ArrayList<>(LIST_SQL_QUERIES_COLUMNS);
            row.addAll(
                    fluentWebElement
                    .find(tagName("td"))
                    .stream()
                    .map(FluentWebElement::getText)
                    .collect(Collectors.toList())
            );
            container.add(row);
        }

        return container;
    }

    public List<String> headers() {
        List<String> headers = new ArrayList<>(LIST_SQL_QUERIES_COLUMNS);
        headers.addAll($("#sqlQueriesListTable th").stream().map(FluentWebElement::getText).collect(Collectors.toList()));
        return headers;
    }

    public void waitForRows(int rowCount) {
        await().atMost(30, SECONDS).until("#sqlQueriesListTableBody tr").hasSize(rowCount);
    }

    public void delete(int row) {
        FluentList<FluentWebElement> fluentWebElements = find("#sqlQueriesListTableBody tr");
        FluentWebElement tr = fluentWebElements.get(row);
        tr.find(className("delete")).click();
    }

    public void executeNow(int row) {
        FluentList<FluentWebElement> fluentWebElements = find("#sqlQueriesListTableBody tr");
        FluentWebElement tr = fluentWebElements.get(row);
        tr.find(className("f-execute-now")).click();
    }

    public void waitForDeleteSuccessMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message").hasText(format(SQL_QUERY_DELETE_SUCCESS_M, label));
    }

    public void waitForExecuteNowSuccessMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message").hasText(format(ONE_OFF_EXECUTION_SUCCESS_MESSAGE_M, label));
    }
}
