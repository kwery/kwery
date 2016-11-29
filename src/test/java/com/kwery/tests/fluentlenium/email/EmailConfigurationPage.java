package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

public class EmailConfigurationPage extends FluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";
    public static final String RADIO_VALIDATION_ERROR_MESSAGE = "Please select one of these options.";

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(className("f-email-configuration")).isDisplayed();
        return true;
    }

    public void submitSmtpConfigurationForm(SmtpConfiguration config) {
        fill(".f-host").with(config.getHost());
        fill(".f-port").with(String.valueOf(config.getPort()));
        click(".f-ssl-" + config.isSsl());
        fill(".f-smtp-username").with(config.getUsername());
        fill(".f-smtp-password").with(config.getPassword());
        click(".f-smtp-configuration-submit");
    }

    public void submitEmptySmtpConfigurationForm() {
        click(".f-smtp-configuration-submit");
    }

    public void submitEmailConfigurationForm(EmailConfiguration emailConfiguration) {
        fill(".f-from-email").with(emailConfiguration.getFrom());
        fill(".f-bcc").with(emailConfiguration.getBcc());
        fill(".f-reply-to").with(emailConfiguration.getReplyTo());
        click(".f-email-configuration-submit");
    }

    public void submitEmptyEmailConfigurationForm() {
        click(".f-email-configuration-submit");
    }

    public void submitTestEmailForm(String email) {
        fill(".f-test-email").with(email);
        click(".f-test-email-submit");
    }

    public void submitEmptyTestEmailForm() {
        click(".f-test-email-submit");
    }

    public void waitForSaveMessage(String message) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message").hasText(message);
    }

    public String validationMessage(SmtpConfigurationFormField field) {
        return $(className(format("%s-validation-message-f", field.name()))).getText();
    }

    public void waitForSmtpConfigurationFormValidationMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".host-validation-message-f").hasText(INPUT_VALIDATION_ERROR_MESSAGE);
    }

    public String validationMessage(EmailConfigurationFormField field) {
        return $(className(format("%s-validation-message-f", field.name()))).getText();
    }

    public void waitForEmailConfigurationFormValidationMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".from-validation-message-f").hasText(INPUT_VALIDATION_ERROR_MESSAGE);
    }

    public boolean isTestEmailConfigurationToFieldDisabled() {
        return !$(className("f-test-email")).first().isEnabled();
    }

    public boolean isTestEmailConfigurationSubmitButtonDisabled() {
        return !$(className("f-test-email-submit")).first().isEnabled();
    }

    public String testEmailToFieldValidationMessage() {
        return $(className("test-email-to-validation-message-f")).first().getText();
    }

    @Override
    public String getUrl() {
        return "/#email/configuration";
    }

    public enum SmtpConfigurationFormField {
        host, port, ssl, username, password
    }

    public enum EmailConfigurationFormField {
        from, bcc, replyTo
    }
}
