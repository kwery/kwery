package com.kwery.tests.dao.jobexecutiondao;

import com.kwery.dao.JobExecutionDao;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.exparity.hamcrest.BeanMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.models.JobExecutionModel.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class JobExecutionDaoQueryTest extends RepoDashDaoTestBase {
    protected JobExecutionDao jobExecutionDao;
    protected JobExecutionModel jobExecutionModel;

    @Before
    public void setUpQueryRunExecutionDaoQueryTest() {
        JobModel jobModel = TestUtil.jobModel();
        jobExecutionModel = TestUtil.jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(JobModel.JOB_TABLE)
                                .columns(JobModel.ID_COLUMN, JobModel.CRON_EXPRESSION_COLUMN ,JobModel.LABEL_COLUMN)
                                .values(jobModel.getId(), jobModel.getCronExpression(), jobModel.getLabel())
                                .build(),
                        insertInto(JobExecutionModel.TABLE)
                                .columns(JobExecutionModel.COLUMN_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_STATUS,
                                        JobExecutionModel.JOB_ID_FK_COLUMN)
                                .values(jobExecutionModel.getId(), jobExecutionModel.getExecutionStart(), jobExecutionModel.getExecutionEnd(),
                                        jobExecutionModel.getExecutionId(), jobExecutionModel.getStatus(), jobModel.getId())
                                .build()
                )
        );
        dbSetup.launch();

        jobExecutionDao = getInstance(JobExecutionDao.class);
    }

    @Test
    public void testGetByExecutionId() {
        JobExecutionModel fromDb = jobExecutionDao.getByExecutionId(jobExecutionModel.getExecutionId());
        jobExecutionModel.getJobModel().setSqlQueries(new HashSet<>());
        Assert.assertThat(fromDb, BeanMatchers.theSameBeanAs(jobExecutionModel));
    }

    @Test
    public void testGetById() {
        JobExecutionModel fromDb = jobExecutionDao.getById(jobExecutionModel.getId());
        jobExecutionModel.getJobModel().setSqlQueries(new HashSet<>());
        Assert.assertThat(fromDb, BeanMatchers.theSameBeanAs(jobExecutionModel));
    }
}
