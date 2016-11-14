package fluentlenium.user;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;

import static fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.id;
import static util.Messages.USER_UPDATE_SUCCESS_M;

public class UpdateUserPage extends FluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        return find(id("createAdminUserForm")).first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#user/update/1";
    }

    public void waitForUsername(String username) {
        await().atMost(30, SECONDS).until("#username").with("value").startsWith(username);
    }

    public void updateForm(String password) {
        fill("#password").with(password);
        find(id("createAdminUser")).click();
    }

    public void waitForSuccessMessage(String username) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(format(USER_UPDATE_SUCCESS_M, username));
    }

    public boolean isUsernameDisabled() {
       return !find(id("username")).first().isEnabled();
    }
}
