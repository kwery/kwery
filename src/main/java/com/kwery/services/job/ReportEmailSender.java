package com.kwery.services.job;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryEmailSettingModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.KweryMailAttachment;
import com.kwery.services.mail.KweryMailAttachmentImpl;
import com.kwery.services.mail.MailService;
import com.kwery.services.scheduler.CsvToHtmlConverter;
import com.kwery.services.scheduler.CsvToHtmlConverterFactory;
import com.kwery.conf.KweryDirectory;
import com.kwery.utils.KweryUtil;
import com.kwery.utils.ReportUtil;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.controllers.MessageKeys.JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING;
import static com.kwery.controllers.MessageKeys.REPORTEMAILSENDER_ATTACHMENT_SKIPPED;
import static com.kwery.utils.KweryUtil.fileName;

@Singleton
public class ReportEmailSender {
    protected Logger logger = LoggerFactory.getLogger(ReportEmailSender.class);

    protected CsvToHtmlConverterFactory csvToHtmlConverterFactory;
    protected final Provider<KweryMail> kweryMailProvider;
    protected final MailService mailService;
    protected final KweryDirectory kweryDirectory;
    protected final Messages messages;

    @Inject
    public ReportEmailSender(CsvToHtmlConverterFactory csvToHtmlConverterFactory, Provider<KweryMail> kweryMailProvider,
                             MailService mailService, KweryDirectory kweryDirectory, Messages messages) {
        this.kweryMailProvider = kweryMailProvider;
        this.mailService = mailService;
        this.kweryDirectory = kweryDirectory;
        this.csvToHtmlConverterFactory = csvToHtmlConverterFactory;
        this.messages = messages;
    }

    public void send(JobExecutionModel jobExecutionModel) {
        JobModel jobModel = jobExecutionModel.getJobModel();

        String subject = jobModel.getTitle() + " - " + new SimpleDateFormat("EEE MMM dd yyyy HH:mm").format(new Date(jobExecutionModel.getExecutionStart()));

        try {
            List<String> emailSnippets = new LinkedList<>();

            List<KweryMailAttachment> attachments = new LinkedList<>();

            boolean hasContent = false;

            boolean attachmentSkipped = false;

            //Done so that report sections in the mail are ordered in the same order as sql queries in report
            for (SqlQueryExecutionModel sqlQueryExecutionModel : ReportUtil.orderedExecutions(jobExecutionModel)) {
                //If this is null, we include in both email and attachments
                SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryExecutionModel.getSqlQuery().getSqlQueryEmailSettingModel();
                if (sqlQueryEmailSettingModel == null || sqlQueryEmailSettingModel.getIncludeInEmailBody()) {
                    emailSnippets.add("<h1>" + sqlQueryExecutionModel.getSqlQuery().getTitle() + "</h1>");

                    if (sqlQueryExecutionModel.getResultFileName() == null) {
                        emailSnippets.add("<div></div>");
                    } else {
                        File resultFile = kweryDirectory.getFile(sqlQueryExecutionModel.getResultFileName());

                        if (KweryUtil.isFileWithinLimits(resultFile)) {
                            CsvToHtmlConverter csvToHtmlConverter = csvToHtmlConverterFactory.create(resultFile);
                            emailSnippets.add(csvToHtmlConverter.convert());
                            hasContent = hasContent || csvToHtmlConverter.isHasContent();
                        } else {
                            String message = messages.get(JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING, Optional.absent()).get();
                            emailSnippets.add(String.format("<p>%s</p>", message));
                            hasContent = true;
                        }
                    }
                }

                if (sqlQueryEmailSettingModel == null || sqlQueryEmailSettingModel.getIncludeInEmailAttachment()) {
                    //Insert queries do not have an output
                    if (sqlQueryExecutionModel.getStatus() == SqlQueryExecutionModel.Status.SUCCESS && !isInsertQuery(sqlQueryExecutionModel)) {
                        File resultFile = kweryDirectory.getFile(sqlQueryExecutionModel.getResultFileName());
                        if (KweryUtil.canFileBeAttached(resultFile)) {
                            KweryMailAttachment attachment = new KweryMailAttachmentImpl();
                            attachment.setName(fileName(sqlQueryExecutionModel.getSqlQuery().getTitle(),
                                    sqlQueryExecutionModel.getJobExecutionModel().getExecutionStart()));
                            attachment.setFile(kweryDirectory.getFile(sqlQueryExecutionModel.getResultFileName()));
                            attachment.setDescription("");
                            attachments.add(attachment);
                        } else {
                            attachmentSkipped = true;
                        }
                    }
                }
            }

            if (attachmentSkipped) {
                String message = messages.get(REPORTEMAILSENDER_ATTACHMENT_SKIPPED, Optional.absent()).get();
                emailSnippets.add(String.format("<p style='color:red'>%s</p>", message));
            }

            //For now do not bother about include in email and attachments while evaluating rules
            if (shouldSend(hasContent, jobModel)) {
                KweryMail kweryMail = kweryMailProvider.get();
                kweryMail.setSubject(subject);
                kweryMail.setBodyHtml(String.join("", emailSnippets));

                //This condition might occur due to email setting rules
                if (kweryMail.getBodyHtml().equals("")) {
                    kweryMail.setBodyHtml(" "); //Causes exception, hence the hack
                }

                jobModel.getEmails().forEach(kweryMail::addTo);
                kweryMail.setAttachments(attachments);

                try {
                    mailService.send(kweryMail);
                    logger.info("Job id {} and execution id {} email sent to {}", jobModel.getId(), jobExecutionModel.getId(), String.join(", ", jobModel.getEmails()));
                } catch (Exception e) {
                    logger.error("Exception while trying to send report email for job id {} and execution id {}", jobModel.getId(), jobExecutionModel.getId(), e);
                }
            }
        } catch (IOException e) {
            logger.error("Exception while trying to convert result to html for job id {} and execution id {}", jobModel.getId(), jobExecutionModel.getId(), e);
        }
    }

    private boolean isInsertQuery(SqlQueryExecutionModel sqlQueryExecutionModel) {
        return sqlQueryExecutionModel.getSqlQuery().getQuery().trim().toLowerCase().startsWith("insert");
    }

    @VisibleForTesting
    public boolean shouldSend(boolean hasContent, JobModel jobModel) {
        if (!jobModel.getRules().isEmpty()) {
            if (!hasContent) {
                String emptyResultSendRule = jobModel.getRules().get(JobModel.Rules.EMPTY_REPORT_NO_EMAIL);
                return !Boolean.valueOf(emptyResultSendRule);
            }
        }
        return true;
    }
}
