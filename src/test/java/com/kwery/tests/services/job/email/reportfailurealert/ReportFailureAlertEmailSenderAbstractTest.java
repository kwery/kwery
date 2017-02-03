package com.kwery.tests.services.job.email.reportfailurealert;

import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.services.job.ReportFailureAlertEmailSender;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.Messages.REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT_M;
import static com.kwery.tests.util.TestUtil.*;

public class ReportFailureAlertEmailSenderAbstractTest extends RepoDashTestBase {
    ReportFailureAlertEmailSender emailSender;
    JobExecutionModel jobExecutionModel;

    @Before
    public void setUp() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobModel.setFailureAlertEmails(ImmutableSet.of("foo@bar.com", "boo@goo.com"));
        jobFailureAlertEmailDbSetUp(jobModel);

        jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionModel.setExecutionStart(1482422361284l); //Thu Dec 22 21:29:21 IST 2016
        jobExecutionModel.setStatus(JobExecutionModel.Status.FAILURE);

        jobExecutionDbSetUp(jobExecutionModel);

        emailSender = getInstance(ReportFailureAlertEmailSender.class);
    }

    public String expectedSubject() {
        return REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT_M + " - " + jobExecutionModel.getJobModel().getTitle() + " - Thu Dec 22 2016 21:29";
    }
}
