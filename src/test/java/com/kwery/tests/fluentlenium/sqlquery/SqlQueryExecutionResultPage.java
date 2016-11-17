package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kwery.tests.fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;

public class SqlQueryExecutionResultPage extends FluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).until("#executionResultTable").isDisplayed();
        return find(By.id("executionResultTable")).first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/1/execution/thik-3456-lkdsjkfkl-lskjdfkl";
    }

    public void waitForResultHeader(int headerCount) {
        await().atMost(30, TimeUnit.SECONDS).until("#resultHeader th").hasSize(headerCount);
    }

    public List<String> resultHeader() {
        List<String> headers = new LinkedList<>();

        for (FluentWebElement header : $("#resultHeader th")) {
            headers.add(header.getText());
        }

        return headers;
    }

    public List<List<String>> resultContent() {
        List<List<String>> container = new LinkedList<>();

        for (FluentWebElement row : $("#resultContent tr")) {
            List<String> content = new LinkedList<>();
            for (FluentWebElement td : row.find(By.tagName("td"))) {
                content.add(td.getText());
            }

            container.add(content);
        }

        return container;
    }
}
