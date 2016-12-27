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

import java.util.HashSet;

import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;

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
        dependentJobModel.setSqlQueries(new HashSet<>());

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

        for (SqlQueryModel sqlQueryModel : dependentJobModel.getSqlQueries()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.sequenceOf(
                            insertInto(JOB_SQL_QUERY_TABLE)
                                    .row()
                                    .column(ID_COLUMN, sqlQueryModel.getId())
                                    .column(JOB_ID_FK_COLUMN, dependentJobModel.getId())
                                    .column(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                                    .end()
                                    .build()
                    )
            ).launch();
        }

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.insertInto(JOB_DEPENDENT_TABLE)
                        .row()
                        .column(JOB_DEPENDENT_TABLE_JOB_ID_FK_COLUMN, jobModel.getId())
                        .column(JOB_DEPENDENT_TABLE_DEPENDENT_JOB_ID_FK_COLUMN, dependentJobModel.getId())
                        .end()
                        .build()
        ).launch();

        jobExecutionDao = getInstance(JobExecutionDao.class);
        jobService = getInstance(JobService.class);
        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
        jobDao = getInstance(JobDao.class);
    }
}
