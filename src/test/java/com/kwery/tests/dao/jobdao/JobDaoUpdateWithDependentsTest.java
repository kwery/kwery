package com.kwery.tests.dao.jobdao;

import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;

public class JobDaoUpdateWithDependentsTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel0;
    protected JobModel jobModel1;
    protected JobModel jobModel2;
    protected Datasource datasource;

    @Before
    public void setUpJobDaoUpdateWithDependentsTest() {
        jobModel0 = jobModelWithoutDependents();
        jobModel0.setSqlQueries(new HashSet<>());

        jobModel1 = jobModelWithoutDependents();
        jobModel1.setSqlQueries(new HashSet<>());

        jobModel2 = jobModelWithoutDependents();
        jobModel2.setSqlQueries(new HashSet<>());

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        Operations.insertInto(JOB_TABLE)
                                .row()
                                    .column(JobModel.ID_COLUMN, jobModel0.getId())
                                    .column(JobModel.CRON_EXPRESSION_COLUMN, jobModel0.getCronExpression())
                                    .column(JobModel.LABEL_COLUMN, jobModel0.getLabel())
                                .end()
                                .row()
                                    .column(JobModel.ID_COLUMN, jobModel1.getId())
                                    .column(JobModel.CRON_EXPRESSION_COLUMN, jobModel1.getCronExpression())
                                    .column(JobModel.LABEL_COLUMN, jobModel1.getLabel())
                                .end()
                                .row()
                                    .column(JobModel.ID_COLUMN, jobModel2.getId())
                                    .column(JobModel.CRON_EXPRESSION_COLUMN, jobModel2.getCronExpression())
                                    .column(JobModel.LABEL_COLUMN, jobModel2.getLabel())
                                .end()
                                .build(),
                        Operations.insertInto(JOB_DEPENDENT_TABLE)
                                .row()
                                    .column(JOB_DEPENDENT_TABLE_JOB_ID_FK_COLUMN, jobModel0.getId())
                                    .column(JOB_DEPENDENT_TABLE_DEPENDENT_JOB_ID_FK_COLUMN, jobModel1.getId())
                                .end()
                        .build()
                )
        ).launch();


        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testAddDependent() throws Exception {
        JobModel toUpdate = jobDao.getJobById(jobModel0.getId());
        JobModel dependent = jobDao.getJobById(jobModel2.getId());
        toUpdate.getDependentJobs().add(dependent);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(toUpdate, JobModel.class);

        toUpdate = jobDao.save(toUpdate);

        assertDbState(JOB_DEPENDENT_TABLE, jobDependentTable(expected), JOB_DEPENDENT_TABLE_ID_COLUMN);
    }

    @Test
    public void testRemoveAndAddDependent() throws Exception {
        JobModel toUpdate = jobDao.getJobById(jobModel0.getId());
        toUpdate.setDependentJobs(new HashSet<>());
        toUpdate.getDependentJobs().add(jobDao.getJobById(jobModel2.getId()));

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(toUpdate, JobModel.class);

        toUpdate = jobDao.save(toUpdate);

        assertDbState(JOB_DEPENDENT_TABLE, jobDependentTable(expected), JOB_DEPENDENT_TABLE_ID_COLUMN);
    }
}
