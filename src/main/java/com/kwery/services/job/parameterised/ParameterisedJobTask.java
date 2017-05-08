package com.kwery.services.job.parameterised;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.services.job.JobTaskFactory;
import com.kwery.services.job.KweryScheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ParameterisedJobTask extends Task {
    protected final KweryScheduler kweryScheduler;
    protected final JobDao jobDao;
    protected final int jobId;
    protected final ParameterCsvExtractor parameterCsvExtractor;
    protected final JobTaskFactory jobTaskFactory;

    @Inject
    public ParameterisedJobTask(KweryScheduler kweryScheduler,
                                JobDao jobDao,
                                ParameterCsvExtractor parameterCsvExtractor,
                                JobTaskFactory jobTaskFactory,
                                @Assisted int jobId) {
        this.kweryScheduler = kweryScheduler;
        this.jobDao = jobDao;
        this.jobId = jobId;
        this.parameterCsvExtractor = parameterCsvExtractor;
        this.jobTaskFactory = jobTaskFactory;
    }

    @Override
    public void execute(TaskExecutionContext context) throws RuntimeException {
        JobModel jobModel = jobDao.getJobById(jobId);
        try {
            List<Map<String, ?>> parameters = parameterCsvExtractor.extract(jobModel.getParameterCsv());
            for (int i = 0; i < parameters.size(); ++i) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                Task task = jobTaskFactory.create(jobModel, parameters.get(i), countDownLatch);
                kweryScheduler.launch(task);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
