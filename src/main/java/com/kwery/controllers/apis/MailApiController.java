package com.kwery.controllers.apis;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.EmailConfigurationExistsException;
import com.kwery.services.mail.EmailConfigurationService;
import com.kwery.services.mail.MultipleEmailConfigurationException;
import com.kwery.services.mail.smtp.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.smtp.SmtpConfigurationAlreadyPresentException;
import com.kwery.services.mail.smtp.SmtpConfigurationNotFoundException;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.views.ActionResult;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;

import static com.kwery.controllers.MessageKeys.EMAIL_CONFIGURATION_SAVED;
import static com.kwery.controllers.MessageKeys.SMTP_CONFIGURATION_ADDED;
import static com.kwery.controllers.MessageKeys.SMTP_CONFIGURATION_ALREADY_PRESENT;
import static com.kwery.controllers.MessageKeys.SMTP_CONFIGURATION_UPDATED;
import static com.kwery.controllers.MessageKeys.SMTP_MULTIPLE_CONFIGURATION;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;

public class MailApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final SmtpService smtpService;
    protected final EmailConfigurationService emailConfigurationService;
    protected final Messages messages;

    @Inject
    public MailApiController(SmtpService smtpService, EmailConfigurationService emailConfigurationService, Messages messages) {
        this.smtpService = smtpService;
        this.emailConfigurationService = emailConfigurationService;
        this.messages = messages;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveSmtpConfiguration(SmtpConfiguration smtpConfiguration, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");

        boolean isUpdate = false;
        if (smtpConfiguration.getId() != null && smtpConfiguration.getId() > 0) {
            isUpdate = true;
        }

        Result json = Results.json();

        ActionResult actionResult = null;

        try {
            smtpService.save(smtpConfiguration);
            String message = "";

            if (isUpdate) {
                message = messages.get(SMTP_CONFIGURATION_UPDATED, context, Optional.of(json)).get();
            } else {
                message = messages.get(SMTP_CONFIGURATION_ADDED, context, Optional.of(json)).get();
            }

            actionResult = new ActionResult(
                    success,
                    message
            );
        } catch (SmtpConfigurationAlreadyPresentException e) {
            actionResult = new ActionResult(
                    failure,
                    messages.get(SMTP_CONFIGURATION_ALREADY_PRESENT, context, Optional.of(json)).get()
            );
        } catch (MultipleSmtpConfigurationFoundException e) {
            actionResult = new ActionResult(
                    failure,
                    messages.get(SMTP_MULTIPLE_CONFIGURATION, context, Optional.of(json)).get()
            );
        }

        if (logger.isTraceEnabled()) logger.trace(">");

        return json.render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getSmtpConfiguration() throws SmtpConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        if (logger.isTraceEnabled()) logger.trace("<");

        SmtpConfiguration smtpConfiguration = smtpService.getSmtpConfiguration();

        if (logger.isTraceEnabled()) logger.trace(">");

        return Results.json().render(smtpConfiguration);

    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveEmailConfiguration(EmailConfiguration emailConfiguration, Context context) throws MultipleEmailConfigurationException, EmailConfigurationExistsException {
        if (logger.isTraceEnabled()) logger.trace("<");
        Result json = Results.json();
        emailConfigurationService.save(emailConfiguration);
        String message = messages.get(EMAIL_CONFIGURATION_SAVED, context, Optional.of(json)).get();
        ActionResult actionResult = new ActionResult(success, message);
        if (logger.isTraceEnabled()) logger.trace(">");
        return json.render(actionResult);
    }
}
