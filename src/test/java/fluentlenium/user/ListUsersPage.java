package fluentlenium.user;

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

public class ListUsersPage extends FluentPage implements RepoDashPage {
    public static final int COLUMNS = 2;

    @Override
    public boolean isRendered() {
        return find(id("usersListTable")).first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#user/list";
    }

    public void waitForRows(int rowCount) {
        await().atMost(30, TimeUnit.SECONDS).until("#usersListTableBody tr").hasSize(rowCount);
    }

    public List<String> headers() {
        List<String> headers = new ArrayList<>(COLUMNS);

        for (FluentWebElement th : $("#usersListTable th")) {
            headers.add(th.getText());
        }

        return headers;
    }

    public List<List<String>> rows() {
        List<List<String>> rows = new LinkedList<>();

        for (FluentWebElement tr : $("#usersListTableBody tr")) {
            List<String> row = new ArrayList<>(COLUMNS);
            row.addAll(tr.find(tagName("td")).stream().map(FluentWebElement::getText).collect(Collectors.toList()));
            rows.add(row);
        }

        return rows;
    }
}
