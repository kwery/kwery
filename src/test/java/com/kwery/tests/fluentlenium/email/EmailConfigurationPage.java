package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#email/configuration")
public class EmailConfigurationPage extends KweryFluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";
    public static final String RADIO_VALIDATION_ERROR_MESSAGE = "Please select one of these options.";

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-email-configuration")).displayed();
        return true;
    }

    public void submitSmtpConfigurationForm(SmtpConfiguration config) {
        if (config.isUseLocalSetting()) {
            clickUseLocalSetting();
        }

        $(".f-host").fill().with(config.getHost());
        $(".f-port").fill().with(String.valueOf(config.getPort()));

        if (!config.isUseLocalSetting()) {
            $(".f-ssl-" + config.isSsl()).click();
            $(".f-smtp-username").fill().with(config.getUsername());
            $(".f-smtp-password").fill().with(config.getPassword());
        }

        clickSmtpFormSubmit();
    }

    public void clickSmtpFormSubmit() {
        $(".f-smtp-configuration-submit").click();
    }

    public void clearHostField() {
        $(".f-host").clear();
    }

    public void clearPortField() {
        $(".f-port").clear();
    }

    public void clickUseLocalSetting() {
        $(".local-setting-f").click();
    }

    public void submitEmptySmtpConfigurationForm() {
        clickSmtpFormSubmit();
    }

    public void submitEmailConfigurationForm(EmailConfiguration emailConfiguration) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-from-email")).clickable();
        $(".f-from-email").fill().with(emailConfiguration.getFrom());
        $(".f-bcc").fill().with(emailConfiguration.getBcc());
        $(".f-reply-to").fill().with(emailConfiguration.getReplyTo());
        $(".f-email-configuration-submit").click();
    }

    public void submitEmptyEmailConfigurationForm() {
        $(".f-email-configuration-submit").click();
    }

    public void submitTestEmailForm(String email) {
        $(".f-test-email").fill().with(email);
        $(".f-test-email-submit").click();
    }

    public void submitEmptyTestEmailForm() {
        $(".f-test-email-submit").click();
    }

    public void waitForSaveMessage(String message) {
        waitForSuccessMessage(message);
    }

    public String validationMessage(SmtpConfigurationFormField field) {
        return $(className(format("%s-validation-message-f", field.name()))).text();
    }

    public void waitForSmtpConfigurationFormValidationMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".host-validation-message-f")).text(INPUT_VALIDATION_ERROR_MESSAGE);
    }

    public String validationMessage(EmailConfigurationFormField field) {
        return $(className(format("%s-validation-message-f", field.name()))).text();
    }

    public void waitForEmailConfigurationFormValidationMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".from-validation-message-f")).text(INPUT_VALIDATION_ERROR_MESSAGE);
    }

    public boolean isTestEmailConfigurationToFieldDisabled() {
        return !$(className("f-test-email")).first().enabled();
    }

    public boolean isTestEmailConfigurationSubmitButtonDisabled() {
        return !$(className("f-test-email-submit")).first().enabled();
    }

    public String testEmailToFieldValidationMessage() {
        return $(className("test-email-to-validation-message-f")).first().text();
    }

    public enum SmtpConfigurationFormField {
        host, port, ssl, username, password
    }

    public enum EmailConfigurationFormField {
        from, bcc, replyTo
    }

    public void assertLocalSmtpDefaultValues() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-host")).value("localhost");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-port")).value("25");
    }
}
