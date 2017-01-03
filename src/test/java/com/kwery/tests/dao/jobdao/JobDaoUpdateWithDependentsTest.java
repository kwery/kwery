package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

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

        jobDbSetUp(ImmutableList.of(jobModel0, jobModel1, jobModel2));

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(JOB_CHILDREN_TABLE)
                                .row()
                                    .column(JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN, jobModel0.getId())
                                    .column(JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN, jobModel1.getId())
                                .end()
                        .build()
                )
        ).launch();

        jobModel0.setChildJobs(new HashSet<>());
        jobModel0.getChildJobs().add(jobModel1);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testAddDependent() throws Exception {
        JobModel toUpdate = jobDao.getJobById(jobModel0.getId());
        JobModel dependent = jobDao.getJobById(jobModel2.getId());
        toUpdate.getChildJobs().add(dependent);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(toUpdate, JobModel.class);

        toUpdate = jobDao.save(toUpdate);

        assertDbState(JOB_CHILDREN_TABLE, jobDependentTable(expected), JOB_CHILDREN_TABLE_ID_COLUMN);
    }

    @Test
    public void testRemoveAndAddDependent() throws Exception {
        JobModel toUpdate = jobDao.getJobById(jobModel0.getId());
        toUpdate.setChildJobs(new HashSet<>());
        toUpdate.getChildJobs().add(jobDao.getJobById(jobModel2.getId()));

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(toUpdate, JobModel.class);

        toUpdate = jobDao.save(toUpdate);

        assertDbState(JOB_CHILDREN_TABLE, jobDependentTable(expected), JOB_CHILDREN_TABLE_ID_COLUMN);
    }
}
