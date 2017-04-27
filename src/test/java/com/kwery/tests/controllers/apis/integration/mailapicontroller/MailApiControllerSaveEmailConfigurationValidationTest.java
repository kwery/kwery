package com.kwery.tests.controllers.apis.integration.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.Messages;
import com.kwery.views.ActionResult;
import ninja.Router;
import org.junit.Test;

import java.text.MessageFormat;

import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_EMPTY_FROM_EMAIL_M;
import static com.kwery.tests.util.Messages.EMAIL_CONFIGURATION_INVALID_EMAIL_M;
import static com.kwery.tests.util.TestUtil.assertActionResult;
import static com.kwery.tests.util.TestUtil.emailConfigurationWithoutId;
import static com.kwery.views.ActionResult.Status.failure;

public class MailApiControllerSaveEmailConfigurationValidationTest extends AbstractPostLoginApiTest {
    @Test
    public void testEmptyFromAddress() {
        String url = getInjector().getInstance(Router.class)
                .getReverseRoute(MailApiController.class, "saveEmailConfiguration");
        EmailConfiguration config = emailConfigurationWithoutId();
        config.setFrom("");
        String response = ninjaTestBrowser.postJson(getUrl(url), config);
        ActionResult expected = new ActionResult(failure, EMAIL_CONFIGURATION_EMPTY_FROM_EMAIL_M);
        assertActionResult(response, expected);
    }

    @Test
    public void testInvalidEmail() {
        String url = getInjector().getInstance(Router.class)
                .getReverseRoute(MailApiController.class, "saveEmailConfiguration");
        EmailConfiguration config = emailConfigurationWithoutId();
        config.setFrom("foo");
        config.setReplyTo("bar  ");
        config.setBcc("abhi@getkwery.com, foo, bar");
        String response = ninjaTestBrowser.postJson(getUrl(url), config);
        ActionResult expected = new ActionResult(failure, MessageFormat.format(EMAIL_CONFIGURATION_INVALID_EMAIL_M, "foo, bar, foo, bar"));
        assertActionResult(response, expected);
    }
}
