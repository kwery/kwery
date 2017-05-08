package com.kwery.services.job;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.kwery.conf.KweryDirectory;
import com.kwery.dao.ReportEmailConfigurationDao;
import com.kwery.dtos.email.ReportEmail;
import com.kwery.dtos.email.ReportEmailSection;
import com.kwery.models.*;
import com.kwery.services.mail.KweryMail;
import com.kwery.services.mail.KweryMailAttachment;
import com.kwery.services.mail.KweryMailAttachmentImpl;
import com.kwery.services.mail.MailService;
import com.kwery.services.mail.converter.CsvToReportEmailSectionConverter;
import com.kwery.services.mail.converter.CsvToReportEmailSectionConverterFactory;
import com.kwery.utils.KweryUtil;
import com.kwery.utils.ReportUtil;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kwery.utils.KweryUtil.fileName;

@Singleton
public class ReportEmailSender {
    protected Logger logger = LoggerFactory.getLogger(ReportEmailSender.class);

    protected CsvToReportEmailSectionConverterFactory csvToReportEmailSectionConverterFactory;
    protected final Provider<KweryMail> kweryMailProvider;
    protected final MailService mailService;
    protected final KweryDirectory kweryDirectory;
    protected final Messages messages;
    protected final ITemplateEngine templateEngine;
    protected final ReportEmailConfigurationDao reportEmailConfigurationDao;

    @Inject
    public ReportEmailSender(CsvToReportEmailSectionConverterFactory csvToReportEmailSectionConverterFactory, Provider<KweryMail> kweryMailProvider,
                             MailService mailService, ReportEmailConfigurationDao reportEmailConfigurationDao, KweryDirectory kweryDirectory, ITemplateEngine templateEngine, Messages messages) {
        this.kweryMailProvider = kweryMailProvider;
        this.mailService = mailService;
        this.kweryDirectory = kweryDirectory;
        this.csvToReportEmailSectionConverterFactory = csvToReportEmailSectionConverterFactory;
        this.messages = messages;
        this.templateEngine = templateEngine;
        this.reportEmailConfigurationDao = reportEmailConfigurationDao;
    }

    public void send(JobExecutionModel jobExecutionModel, List<String> emails) {
        JobModel jobModel = jobExecutionModel.getJobModel();

        String subject = jobModel.getTitle() + " - " + new SimpleDateFormat("EEE MMM dd yyyy HH:mm").format(new Date(jobExecutionModel.getExecutionStart()));

        try {
            ReportEmail reportEmail = new ReportEmail();

            ReportEmailConfigurationModel reportEmailConfigurationModel = reportEmailConfigurationDao.get();
            if (reportEmailConfigurationModel != null && !"".equals(reportEmailConfigurationModel.getLogoUrl())) {
                reportEmail.setLogoUrl(reportEmailConfigurationModel.getLogoUrl());
            }

            List<KweryMailAttachment> attachments = new LinkedList<>();

            boolean hasContent = false;

            boolean attachmentSkipped = false;

            //Done so that report sections in the mail are ordered in the same order as sql queries in report
            for (SqlQueryExecutionModel sqlQueryExecutionModel : ReportUtil.orderedExecutions(jobExecutionModel)) {
                //If this is null, we include in both email and attachments
                SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryExecutionModel.getSqlQuery().getSqlQueryEmailSettingModel();
                if (sqlQueryEmailSettingModel == null || sqlQueryEmailSettingModel.getIncludeInEmailBody()) {
                    ReportEmailSection section = new ReportEmailSection();

                    if (sqlQueryExecutionModel.getResultFileName() != null) {
                        File resultFile = kweryDirectory.getFile(sqlQueryExecutionModel.getResultFileName());
                        if (KweryUtil.isFileWithinLimits(resultFile)) {
                            CsvToReportEmailSectionConverter converter = csvToReportEmailSectionConverterFactory.create(resultFile);
                            section = converter.convert();
                            hasContent = hasContent || !section.getRows().isEmpty();
                        } else {
                            section = new ReportEmailSection();
                            section.setContentTooLarge(true);
                            hasContent = true;
                        }
                    }

                    section.setTitle(sqlQueryExecutionModel.getSqlQuery().getTitle());

                    if (sqlQueryEmailSettingModel != null && sqlQueryEmailSettingModel.isSingleResultStyling()) {
                        section.setIgnoreHeader(true);
                    }

                    reportEmail.getReportEmailSections().add(section);
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
                reportEmail.setAttachmentSkipped(true);
            }

            //For now do not bother about include in email and attachments while evaluating rules
            if (shouldSend(hasContent, jobModel)) {
                KweryMail kweryMail = kweryMailProvider.get();
                kweryMail.setSubject(subject);

                Context context = new Context();
                context.setVariable("reportEmail", reportEmail);
                kweryMail.setBodyHtml(templateEngine.process("report", context));

                //This condition might occur due to email setting rules
                if (kweryMail.getBodyHtml().equals("")) {
                    kweryMail.setBodyHtml(" "); //Causes exception, hence the hack
                }

                jobModel.getEmails().forEach(kweryMail::addTo);

                emails.forEach(kweryMail::addTo);

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
