package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.fluentlenium.job.reportlist.ActionResultComponent;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.fluentlenium.core.hook.wait.WaitHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwery.tests.fluentlenium.user.UserListPage.UserList.editLink;
import static com.kwery.tests.fluentlenium.user.UserListPage.UserList.username;
import static com.kwery.tests.util.Messages.USER_DELETE_SUCCESS_M;
import static com.kwery.tests.util.Messages.USER_DELETE_YOURSELF_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/list")
public class UserListPage extends KweryFluentPage implements RepoDashPage {
    public static final int COLUMNS = 2;

    protected ActionResultComponent actionResultComponent;

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".users-list-table-tbody-f")).displayed();
        waitForModalDisappearance();
        return true;
    }

    public List<String> headers() {
        List<String> headers = new ArrayList<>(COLUMNS);

        for (FluentWebElement th : $("#usersListTable th")) {
            headers.add(th.text());
        }

        return headers;
    }

    public void delete(int row) {
        el("div", withClass().contains(String.format("user-list-%d-f", row))).el("button.delete-f").withHook(WaitHook.class).click();
    }

    public void assertDeleteYourselfMessage() {
        actionResultComponent.assertFailureMessage(USER_DELETE_YOURSELF_M);
    }

    public void assertDeleteSuccessMessage(String username) {
        actionResultComponent.assertSuccessMessage(format(USER_DELETE_SUCCESS_M, username));
    }

    public void assertUserList(int row, Map<UserList, ?> map) {
        assertThat(el(String.format(".user-list-%d-f .username-f", row), withText(String.valueOf(map.get(username))))).isDisplayed();
        assertThat(el(String.format(".user-list-%d-f .edit-f", row), with("href").contains(String.valueOf(map.get(editLink))))).isDisplayed();
    }

    public Map<UserList, ?> map(User user) {
        Map map = new HashMap();
        map.put(username, user.getUsername());
        map.put(editLink, String.format("/#user/%d", user.getId()));
        return map;
    }

    public enum UserList {
        username, editLink, deleteLink
    }
}
