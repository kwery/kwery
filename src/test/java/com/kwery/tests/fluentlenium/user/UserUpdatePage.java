package com.kwery.tests.fluentlenium.user;

import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.Messages.USER_UPDATE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#user/{userId}")
public class UserUpdatePage extends UserAddPage {
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
}
