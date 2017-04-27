package com.kwery.controllers.apis;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.kwery.dao.EmailConfigurationDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.services.EmptyFromEmailException;
import com.kwery.services.mail.InvalidEmailException;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.KweryMailImpl;
import com.kwery.services.mail.MailService;
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
import ninja.i18n.Messages;
import ninja.params.PathParam;
import org.thymeleaf.ITemplateEngine;

import javax.persistence.NoResultException;

import static com.kwery.controllers.MessageKeys.*;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;
import static ninja.Results.json;

public class MailApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final SmtpService smtpService;
    protected final EmailConfigurationDao emailConfigurationDao;
    protected final Messages messages;
    protected final MailService mailService;
    protected final ITemplateEngine templateEngine;

    @Inject
    public MailApiController(SmtpService smtpService, EmailConfigurationDao emailConfigurationDao,
                             ITemplateEngine templateEngine, MailService mailService, Messages messages) {
        this.smtpService = smtpService;
        this.emailConfigurationDao = emailConfigurationDao;
        this.mailService = mailService;
        this.messages = messages;
        this.templateEngine = templateEngine;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveSmtpConfiguration(SmtpConfiguration smtpConfiguration, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");

        boolean isUpdate = false;
        if (smtpConfiguration.getId() != null && smtpConfiguration.getId() > 0) {
            isUpdate = true;
        }

        Result json = json();

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
    public Result getSmtpConfiguration() throws MultipleSmtpConfigurationFoundException {
        if (logger.isTraceEnabled()) logger.trace("<");

        SmtpConfiguration smtpConfiguration = null;
        try {
            smtpConfiguration = smtpService.getSmtpConfiguration();
        } catch (SmtpConfigurationNotFoundException e) {
            //Ignore
            //TODO - Remove this stupid exception, stupid design :@
        }

        if (logger.isTraceEnabled()) logger.trace(">");

        return json().render(smtpConfiguration);

    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveEmailConfiguration(EmailConfiguration emailConfiguration, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");
        Result json = json();
        ActionResult actionResult = null;
        try {
            emailConfigurationDao.save(emailConfiguration);
            String message = messages.get(EMAIL_CONFIGURATION_SAVED, context, Optional.of(json)).get();
            actionResult = new ActionResult(success, message);
        } catch (InvalidEmailException ie) {
            String message = messages.get(EMAIL_CONFIGURATION_INVALID_EMAIL, context, Optional.of(json),
                    Joiner.on(", ").join(ie.getInvalids())).get();
            actionResult = new ActionResult(failure, message);
        } catch (EmptyFromEmailException efe) {
            String message = messages.get(EMAIL_CONFIGURATION_EMPTY_FROM_EMAIL, context, Optional.of(json)).get();
            actionResult = new ActionResult(failure, message);
        }
        if (logger.isTraceEnabled()) logger.trace(">");
        return json.render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getEmailConfiguration() {
        if (logger.isTraceEnabled()) logger.trace("<");
        Result json = json();
        EmailConfiguration emailConfiguration = null;
        try {
            emailConfiguration = emailConfigurationDao.get();
        } catch (NoResultException e) {
            //Ignore
        }
        if (logger.isTraceEnabled()) logger.trace(">");
        return json.render(emailConfiguration);
    }

    public Result testEmailConfiguration(@PathParam("toEmail") String toEmail, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");
        Result json = json();

        KweryMail mail = new KweryMailImpl();
        mail.addTo(toEmail);

        String subject = messages.get(EMAIL_TEST_SUBJECT, context, Optional.of(json)).get();
        mail.setSubject(subject);

        org.thymeleaf.context.Context emailContext = new  org.thymeleaf.context.Context();
        mail.setBodyHtml(templateEngine.process("test", emailContext));

        ActionResult actionResult = null;

        try {
            mailService.send(mail);
            actionResult = new ActionResult(
                    success,
                    messages.get(EMAIL_TEST_SUCCESS, context, Optional.of(json)).get()
            );
        } catch (Exception e) {
            logger.error("Error while sending test mail to {}", toEmail, e);
            actionResult = new ActionResult(
                    failure,
                    messages.get(EMAIL_TEST_FAILURE, context, Optional.of(json)).get()
            );
        }

        if (logger.isTraceEnabled()) logger.trace(">");
        return json.render(actionResult);
    }

    public MailService getMailService() {
        return mailService;
    }
}
