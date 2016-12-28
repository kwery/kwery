package com.kwery.services.job;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.MailService;
import com.kwery.services.scheduler.JsonToHtmlTableConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class ReportEmailSender {
    protected Logger logger = LoggerFactory.getLogger(ReportEmailSender.class);

    protected final JsonToHtmlTableConvertor jsonToHtmlTableConvertor;
    protected final Provider<KweryMail> kweryMailProvider;
    protected final MailService mailService;

    @Inject
    public ReportEmailSender(JsonToHtmlTableConvertor jsonToHtmlTableConvertor, Provider<KweryMail> kweryMailProvider, MailService mailService) {
        this.jsonToHtmlTableConvertor = jsonToHtmlTableConvertor;
        this.kweryMailProvider = kweryMailProvider;
        this.mailService = mailService;
    }

    public void send(JobExecutionModel jobExecutionModel) {
        JobModel jobModel = jobExecutionModel.getJobModel();

        String subject = new SimpleDateFormat("EEE MMM dd yyyy HH:mm").format(new Date(jobExecutionModel.getExecutionStart()))
                + " - " + jobModel.getTitle();

        try {
            List<String> emailSnippets = new LinkedList<>();

            for (SqlQueryExecutionModel sqlQueryExecutionModel : jobExecutionModel.getSqlQueryExecutionModels()) {
                emailSnippets.add("<h1>" + sqlQueryExecutionModel.getSqlQuery().getTitle() + "</h1>");

                if (sqlQueryExecutionModel.getResult() == null) {
                    emailSnippets.add("<div></div>");
                } else {
                    emailSnippets.add(jsonToHtmlTableConvertor.convert(sqlQueryExecutionModel.getResult()));
                }

            }

            KweryMail kweryMail = kweryMailProvider.get();
            kweryMail.setSubject(subject);
            kweryMail.setBodyHtml(String.join("", emailSnippets));
            jobModel.getEmails().forEach(kweryMail::addTo);

            try {
                mailService.send(kweryMail);
                logger.info("Job id {} and execution id {} email sent to {}", jobModel.getId(), jobExecutionModel.getId(), String.join(", ", jobModel.getEmails()));
            } catch (Exception e) {
                logger.error("Exception while trying to send report email for job id {} and execution id {}", jobModel.getId(), jobExecutionModel.getId(), e);
            }
        } catch (IOException e) {
            logger.error("Exception while trying to convert result to html for job id {} and execution id {}", jobModel.getId(), jobExecutionModel.getId(), e);
        }
    }
}
