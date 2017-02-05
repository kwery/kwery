package com.kwery.services.job;

import com.google.common.annotations.VisibleForTesting;
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
import com.kwery.services.scheduler.JsonToCsvConverter;
import com.kwery.services.scheduler.JsonToHtmlTableConverter;
import com.kwery.services.scheduler.JsonToHtmlTableConverterFactory;
import com.kwery.utils.ReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.utils.KweryUtil.fileName;

@Singleton
public class ReportEmailSender {
    protected Logger logger = LoggerFactory.getLogger(ReportEmailSender.class);

    protected final JsonToHtmlTableConverterFactory jsonToHtmlTableConverterFactory;
    protected final JsonToCsvConverter jsonToCsvConverter;
    protected final Provider<KweryMail> kweryMailProvider;
    protected final MailService mailService;

    @Inject
    public ReportEmailSender(JsonToHtmlTableConverterFactory jsonToHtmlTableConverterFactory, JsonToCsvConverter jsonToCsvConverter,
                             Provider<KweryMail> kweryMailProvider, MailService mailService) {
        this.jsonToHtmlTableConverterFactory = jsonToHtmlTableConverterFactory;
        this.jsonToCsvConverter = jsonToCsvConverter;
        this.kweryMailProvider = kweryMailProvider;
        this.mailService = mailService;
    }

    public void send(JobExecutionModel jobExecutionModel) {
        JobModel jobModel = jobExecutionModel.getJobModel();

        String subject = jobModel.getTitle() + " - " + new SimpleDateFormat("EEE MMM dd yyyy HH:mm").format(new Date(jobExecutionModel.getExecutionStart()));

        try {
            List<String> emailSnippets = new LinkedList<>();

            List<KweryMailAttachment> attachments = new LinkedList<>();

            boolean emptyResult = false;

            //Done so that report sections in the mail are ordered in the same order as sql queries in report
            for (SqlQueryExecutionModel sqlQueryExecutionModel : ReportUtil.orderedExecutions(jobExecutionModel)) {
                //If this is null, we include in both email and attachments
                SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryExecutionModel.getSqlQuery().getSqlQueryEmailSettingModel();
                if (sqlQueryEmailSettingModel == null || sqlQueryEmailSettingModel.getIncludeInEmailBody()) {
                    emailSnippets.add("<h1>" + sqlQueryExecutionModel.getSqlQuery().getTitle() + "</h1>");

                    if (sqlQueryExecutionModel.getResult() == null) {
                        emailSnippets.add("<div></div>");
                    } else {
                        JsonToHtmlTableConverter jsonToHtmlTableConverter = jsonToHtmlTableConverterFactory.create(sqlQueryExecutionModel.getResult());
                        emailSnippets.add(jsonToHtmlTableConverter.convert());
                        emptyResult = emptyResult || jsonToHtmlTableConverter.isHasContent();
                    }
                }

                if (sqlQueryEmailSettingModel == null || sqlQueryEmailSettingModel.getIncludeInEmailAttachment()) {
                    //We do not want to send out attachments if the execution did not yield in any result, happens in case of insert queries
                    if (sqlQueryExecutionModel.getResult() != null) {
                        KweryMailAttachment attachment = new KweryMailAttachmentImpl();
                        attachment.setName(fileName(sqlQueryExecutionModel.getSqlQuery().getTitle(),
                                sqlQueryExecutionModel.getJobExecutionModel().getExecutionStart()));
                        attachment.setContent(jsonToCsvConverter.convert(sqlQueryExecutionModel.getResult()));
                        attachment.setDescription("");
                        attachments.add(attachment);
                    }
                }
            }

            //For now do not bother about include in email and attachments while evaluation rules
            if (shouldSend(emptyResult, jobModel)) {
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
