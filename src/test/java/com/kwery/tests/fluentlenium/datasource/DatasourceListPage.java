package com.kwery.tests.fluentlenium.datasource;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kwery.tests.util.Messages.DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M;
import static com.kwery.tests.util.Messages.DATASOURCE_DELETE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class DatasourceListPage extends KweryFluentPage implements RepoDashPage {
    public static final int COLUMNS = 5;

    @Override
    public boolean isRendered() {
        return find(id("datasourcesListTable")).first().displayed();
    }

    @Override
    public String getUrl() {
        return "/#datasource/list";
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
        FluentList<FluentWebElement> fluentWebElements = find("#datasourcesListTableBody tr");
        FluentWebElement tr = fluentWebElements.get(row);
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-delete")).clickable();
        tr.find(className("f-delete")).click();
    }

    public void waitForDeleteSuccessMessage(String label) {
        super.waitForSuccessMessage(format(DATASOURCE_DELETE_SUCCESS_M, label));
    }

    public void waitForDeleteFailureSqlQueryMessage() {
        super.waitForFailureMessage(DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M);
    }
}
