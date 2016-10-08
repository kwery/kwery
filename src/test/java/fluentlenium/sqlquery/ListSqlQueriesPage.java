package fluentlenium.sqlquery;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        await().atMost(30, TimeUnit.SECONDS).until("#sqlQueriesListTableBody tr").hasSize(rowCount);
    }
}
