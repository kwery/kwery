package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.lang.String.format;

public class SqlQueryExecutionResultPage extends FluentPage implements RepoDashPage {
    protected int sqlQueryId;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).until($("#executionResultTable")).displayed();
        return find(By.id("executionResultTable")).first().displayed();
    }

    @Override
    public String getUrl() {
        return format("/#sql-query/%d/execution/thik-3456-lkdsjkfkl-lskjdfkl", getSqlQueryId());
    }

    public void waitForResultHeader(int headerCount) {
        await().atMost(30, TimeUnit.SECONDS).until($("#resultHeader th")).size(headerCount);
    }

    public List<String> resultHeader() {
        List<String> headers = new LinkedList<>();

        for (FluentWebElement header : $("#resultHeader th")) {
            headers.add(header.text());
        }

        return headers;
    }

    public List<List<String>> resultContent() {
        List<List<String>> container = new LinkedList<>();

        for (FluentWebElement row : $("#resultContent tr")) {
            List<String> content = new LinkedList<>();
            for (FluentWebElement td : row.find(By.tagName("td"))) {
                content.add(td.text());
            }

            container.add(content);
        }

        return container;
    }

    public int getSqlQueryId() {
        return sqlQueryId;
    }

    public void setSqlQueryId(int sqlQueryId) {
        this.sqlQueryId = sqlQueryId;
    }
}
