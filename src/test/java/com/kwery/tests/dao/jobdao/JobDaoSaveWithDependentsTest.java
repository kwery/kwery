package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class JobDaoSaveWithDependentsTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;

    @Before
    public void setUpJobDaoSaveTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(new HashSet<>());

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                insertInto(JOB_TABLE)
                        .row()
                        .column(ID_COLUMN, jobModel.getId())
                        .column(JobModel.CRON_EXPRESSION_COLUMN, jobModel.getCronExpression())
                        .column(JobModel.LABEL_COLUMN, jobModel.getLabel())
                        .end()
                        .build()
        ).launch();

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        JobModel newJobModel = jobModelWithoutIdWithoutDependents();
        newJobModel.setSqlQueries(new HashSet<>());
        newJobModel.setDependentJobs(new HashSet<>());
        newJobModel.getDependentJobs().add(jobModel);

        jobDao.save(newJobModel);

        assertDbState(JOB_TABLE, jobTable(ImmutableList.of(jobModel, newJobModel)));
        assertDbState(JOB_DEPENDENT_TABLE, jobDependentTable(newJobModel), "id");
    }
}
