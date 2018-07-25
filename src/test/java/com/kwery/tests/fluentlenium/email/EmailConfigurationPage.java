package com.kwery.tests.fluentlenium.email;

import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.hook.wait.Wait;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withTextContent;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#email/configuration")
public class EmailConfigurationPage extends KweryFluentPage implements RepoDashPage {
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

    public void assertNonEmptyValidationMessage(SmtpConfigurationFormField field) {
        assertThat(el("div", withClass().contains(String.format("%s-validation-message-f", field.name())), withTextContent().notContains("")));
    }

    public void assertEmptyValidationMessage(SmtpConfigurationFormField field) {
        assertThat(el("div", withClass().contains(String.format("%s-validation-message-f", field.name())), withTextContent().equalTo("")));
    }

    public void assertNonEmptySmtpConfigurationFormValidationMessage() {
        assertThat(el("div", withClass().contains("host-validation-message-f"), withTextContent().notContains("")));
    }

    public void assertNonEmptyValidationMessage(EmailConfigurationFormField field) {
        assertThat(el("div", withClass().contains(String.format("%s-validation-message-f", field.name())), withTextContent().notContains("")));
    }

    public void assertEmptyValidationMessage(EmailConfigurationFormField field) {
        assertThat(el("div", withClass().contains(String.format("%s-validation-message-f", field.name())), withTextContent().equalTo("")));
    }

    public void assertNonEmptyEmailConfigurationFormValidationMessage() {
        assertThat(el("div", withClass().contains("from-validation-message-f"), withTextContent().notContains("")));
    }

    public boolean isTestEmailConfigurationToFieldDisabled() {
        return !$(className("f-test-email")).first().enabled();
    }

    public boolean isTestEmailConfigurationSubmitButtonDisabled() {
        return !$(className("f-test-email-submit")).first().enabled();
    }

    public void assertNonEmptyEmailToFieldValidationMessage() {
        assertThat(el("div", withClass().contains("test-email-to-validation-message-f"), withTextContent().notContains("")));
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
