package controllers.fluentlenium;

import controllers.util.Messages;
import junit.framework.TestCase;
import org.junit.Test;

import static controllers.util.HtmlId.CREATE_ADMIN_USER_I;
import static controllers.util.HtmlId.HERO_TEXT_I;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class IndexControllerFluentLeniumTest extends DashRepoFluentLeniumTest {
    @Test
    public void index() {
        goTo(getServerAddress());
        assertTrue(title().contains("Home page"));
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(htmlId(HERO_TEXT_I)).isPresent();
        TestCase.assertEquals("Hero text is present in index page", Messages.INSTALLATION_WELCOME_M, $(htmlId(HERO_TEXT_I)).getText());
        assertEquals("Create admin user button present in index page", Messages.CREATE_ADMIN_USER_M.toUpperCase(), $(htmlId(CREATE_ADMIN_USER_I)).getText());
        click($(htmlId(CREATE_ADMIN_USER_I)));
        await().atMost(TIMEOUT_SECONDS, SECONDS);
        assertEquals("URL changed to create admin user fragment", getServerAddress() + "#onboarding/create-admin-user", url());
    }
}
