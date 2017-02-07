package com.kwery.tests.fluentlenium.user;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kwery.tests.util.Messages.USER_DELETE_SUCCESS_M;
import static com.kwery.tests.util.Messages.USER_DELETE_YOURSELF_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/list")
public class UserListPage extends KweryFluentPage implements RepoDashPage {
    public static final int COLUMNS = 2;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".users-list-table-tbody-f")).displayed();
        waitForModalDisappearance();
        return true;
    }

    public void waitForRows(int rowCount) {
        await().atMost(30, SECONDS).until($(".users-list-table-tbody-f tr")).size(rowCount);
    }

    public List<String> headers() {
        List<String> headers = new ArrayList<>(COLUMNS);

        for (FluentWebElement th : $("#usersListTable th")) {
            headers.add(th.text());
        }

        return headers;
    }

    public List<List<String>> rows() {
        List<List<String>> rows = new LinkedList<>();

        for (FluentWebElement tr : $(".users-list-table-tbody-f tr")) {
            List<String> row = new ArrayList<>(COLUMNS);
            row.addAll(tr.find(tagName("td")).stream().map(FluentWebElement::text).collect(Collectors.toList()));
            rows.add(row);
        }

        return rows;
    }

    public void delete(int row) {
        FluentList<FluentWebElement> fluentWebElements = find(".users-list-table-tbody-f tr");
        FluentWebElement tr = fluentWebElements.get(row);
        tr.find(className("f-delete")).click();
    }

    public void waitForDeleteSuccessMessage(String username) {
        super.waitForSuccessMessage(format(USER_DELETE_SUCCESS_M, username));
    }

    public void waitForDeleteYourselfMessage() {
        super.waitForFailureMessage(USER_DELETE_YOURSELF_M);
    }
}
