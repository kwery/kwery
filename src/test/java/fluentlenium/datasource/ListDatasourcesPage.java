package fluentlenium.datasource;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.id;

public class ListDatasourcesPage extends FluentPage implements RepoDashPage {
    public static final int COLUMNS = 5;

    @Override
    public boolean isRendered() {
        return find(id("datasourcesListTable")).first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#datasource/list";
    }

    public List<String> headers() {
        List<String> headers = new ArrayList<>(COLUMNS);

        for (FluentWebElement header : $("#datasourcesListTable tr th")) {
            headers.add(header.getText());
        }

        return headers;
    }

    public List<List<String>> rows() {
        List<List<String>> rows = new LinkedList<>();

        for (FluentWebElement tr : $("#datasourcesListTableBody tr")) {
            List<String> row = new ArrayList<>(COLUMNS);
            row.addAll(tr.find(By.tagName("td")).stream().map(FluentWebElement::getText).collect(Collectors.toList()));
            rows.add(row);
        }

        return rows;
    }

    public void waitForRows(int rowCount) {
        await().atMost(30, TimeUnit.SECONDS).until("#datasourcesListTableBody tr").hasSize(rowCount);
    }
}
