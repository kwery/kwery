package com.kwery.tests.services.job;

import com.kwery.dao.JobDao;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;

import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;

public abstract class JobServiceJobSetUpWithDependentsAbstractTest extends JobServiceJobSetUpAbstractTest {
    protected JobModel dependentJobModel;
    protected JobExecutionDao jobExecutionDao;
    protected JobService jobService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected JobDao jobDao;

    protected int sqlQueryId2;
    protected int sqlQueryId3;

    @Before
    public void setUpJobServiceJobSetUpWithDependentsAbstractTest() {
        dependentJobModel = jobModelWithoutDependents();
        dependentJobModel.setCronExpression("");

        for (int i = 0; i < 2; ++i) {
            SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
            if (i == 0) {
                sqlQueryId2 = sqlQueryModel.getId();
            } else {
                sqlQueryId3 = sqlQueryModel.getId();
            }
            sqlQueryModel.setQuery(getQuery());
            dependentJobModel.getSqlQueries().add(sqlQueryModel);
        }

        jobDbSetUp(dependentJobModel);
        sqlQueryDbSetUp(dependentJobModel.getSqlQueries());
        jobSqlQueryDbSetUp(dependentJobModel);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.insertInto(JOB_CHILDREN_TABLE)
                        .row()
                        .column(JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN, jobModel.getId())
                        .column(JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN, dependentJobModel.getId())
                        .end()
                        .build()
        ).launch();

        jobExecutionDao = getInstance(JobExecutionDao.class);
        jobService = getInstance(JobService.class);
        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
        jobDao = getInstance(JobDao.class);
    }
}
