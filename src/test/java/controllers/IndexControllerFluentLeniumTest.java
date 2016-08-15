package controllers;

import ninja.NinjaFluentLeniumTest;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class IndexControllerFluentLeniumTest extends DashRepoFluentLeniumTest {
    @Test
    public void index() {
        goTo(getServerAddress());
        assertTrue(title().contains("Home page"));
        await().atMost(5, TimeUnit.SECONDS).until("#heroText").isPresent();
        assertEquals("Hero text is present in index page", "Welcome to RepoDash, reporting, dashboarding and visualistion made easy. As the first step, let us create an administrative user to manage the application.", $("#heroText").getText());
        assertEquals("Create admin user button present in index page", "CREATE ADMIN USER", $("#createAdminUser").getText());
        click($("#createAdminUser"));
        await().atMost(5, TimeUnit.SECONDS);
        assertEquals("URL changed to create admin user fragment", getServerAddress() + "#onboarding/create-admin-user", url());
    }
}
