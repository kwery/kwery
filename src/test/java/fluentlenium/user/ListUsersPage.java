package fluentlenium.user;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.tagName;
import static util.Messages.USER_DELETE_SUCCESS_M;
import static util.Messages.USER_DELETE_YOURSELF_M;

public class ListUsersPage extends FluentPage implements RepoDashPage {
    public static final int COLUMNS = 3;

    @Override
    public boolean isRendered() {
        return find(id("usersListTable")).first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#user/list";
    }

    public void waitForRows(int rowCount) {
        await().atMost(30, SECONDS).until("#usersListTableBody tr").hasSize(rowCount);
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

    public void delete(int row) {
        FluentList<FluentWebElement> fluentWebElements = find("#usersListTableBody tr");
        FluentWebElement tr = fluentWebElements.get(row);
        tr.find(className("delete")).click();
    }

    public void waitForDeleteSuccessMessage(String username) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_info p").hasText(format(USER_DELETE_SUCCESS_M, username));
    }

    public void waitForDeleteYourselfMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_error p").hasText(USER_DELETE_YOURSELF_M);
    }
}
