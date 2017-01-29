package com.kwery.tests.fluentlenium.user;

import static com.kwery.tests.util.Messages.USER_UPDATE_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class UserUpdatePage extends UserAddPage {
    protected int userId;

    @Override
    public String getUrl() {
        return "/#user/" + getUserId();
    }

    public void waitForUsername(String username) {
        await().atMost(30, SECONDS).until($(".username-f")).attribute("value").startsWith(username);
    }

    public void updateForm(String password) {
        waitForModalDisappearance();
        $(".password-f").fill().with(password);
        $(".user-save-f").click();
    }

    public void waitForSuccessMessage(String username) {
        super.waitForSuccessMessage(format(USER_UPDATE_SUCCESS_M, username));
    }

    public boolean isUsernameDisabled() {
       return !find((".username-f")).first().enabled();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
