package com.kwery.tests.services.job;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobService;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;

import java.util.HashSet;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobModel.ID_COLUMN;
import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public abstract class JobServiceJobSetUpWithDependentsAbstractTest extends JobServiceJobSetUpAbstractTest {
    protected JobModel dependentJobModel;
    protected JobExecutionDao jobExecutionDao;
    protected JobService jobService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected int sqlQueryId2 = 3;
    protected int sqlQueryId3 = 4;

    @Before
    public void setUpJobServiceJobSetUpWithDependentsAbstractTest() {
        dependentJobModel = new JobModel();
        dependentJobModel.setId(2);
        dependentJobModel.setCronExpression("");
        dependentJobModel.setLabel("dependetJob");
        dependentJobModel.setSqlQueries(new HashSet<>());


        for (int sqlQueryId : ImmutableList.of(sqlQueryId2, sqlQueryId3)) {
            SqlQueryModel sqlQueryModel = new SqlQueryModel();
            sqlQueryModel.setId(sqlQueryId);
            sqlQueryModel.setLabel("select" + sqlQueryId);
            sqlQueryModel.setQuery(getQuery());
            sqlQueryModel.setDatasource(datasource);

            dependentJobModel.getSqlQueries().add(sqlQueryModel);
        }

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                insertInto(JOB_TABLE)
                        .row()
                        .column(ID_COLUMN, dependentJobModel.getId())
                        .column(JobModel.CRON_EXPRESSION_COLUMN, dependentJobModel.getCronExpression())
                        .column(JobModel.LABEL_COLUMN, dependentJobModel.getLabel())
                        .end()
                        .build()
        ).launch();

        for (SqlQueryModel sqlQueryModel : dependentJobModel.getSqlQueries()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.sequenceOf(
                            insertInto(SQL_QUERY_TABLE)
                                    .row()
                                    .column(ID_COLUMN, sqlQueryModel.getId())
                                    .column(SqlQueryModel.LABEL_COLUMN, sqlQueryModel.getLabel())
                                    .column(QUERY_COLUMN, sqlQueryModel.getQuery())
                                    .column(DATASOURCE_ID_FK_COLUMN, sqlQueryModel.getDatasource().getId())
                                    .end()
                                    .build(),
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
    }
}
