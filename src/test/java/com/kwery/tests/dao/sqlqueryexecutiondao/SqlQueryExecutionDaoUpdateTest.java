package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.SqlQueryExecutionModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryExecutionTable;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.sqlQueryExecutionModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class SqlQueryExecutionDaoUpdateTest extends SqlQueryExecutionDaoPersistTest {
    protected SqlQueryExecutionModel sqlQueryExecutionModel;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected JobExecutionModel jobExecutionModel0;

    @Before
    public void setUpSqlQueryExecutionDaoUpdateTest() {
        sqlQueryExecutionModel = sqlQueryExecutionModel();
        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel0 = jobExecutionModel();
        jobExecutionModel0.setJobModel(jobModel);

        new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK, SqlQueryExecutionModel.COLUMN_JOB_EXECUTION_ID_FK)
                                .values(sqlQueryExecutionModel.getId(), sqlQueryExecutionModel.getExecutionEnd(), sqlQueryExecutionModel.getExecutionId(), sqlQueryExecutionModel.getExecutionStart(), sqlQueryExecutionModel.getResult(), sqlQueryExecutionModel.getStatus(), sqlQueryExecutionModel.getSqlQuery().getId(), sqlQueryExecutionModel.getJobExecutionModel().getId())
                                .build(),
                        insertInto(JobExecutionModel.TABLE)
                                .columns(JobExecutionModel.COLUMN_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_STATUS,
                                        JobExecutionModel.JOB_ID_FK_COLUMN)
                                .values(jobExecutionModel0.getId(), jobExecutionModel0.getExecutionStart(), jobExecutionModel0.getExecutionEnd(),
                                        jobExecutionModel0.getExecutionId(), jobExecutionModel0.getStatus(), jobExecutionModel0.getJobModel().getId()).build()
                )
        ).launch();

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void test() throws Exception {
        SqlQueryExecutionModel updated = TestUtil.sqlQueryExecutionModelWithoutId();
        updated.setId(sqlQueryExecutionModel.getId());
        updated.setJobExecutionModel(jobExecutionModel0);
        updated.setSqlQuery(sqlQuery);

        DozerBeanMapper mapper = new DozerBeanMapper();
        SqlQueryExecutionModel expected = mapper.map(updated, SqlQueryExecutionModel.class);

        sqlQueryExecutionDao.save(updated);

        assertDbState(SqlQueryExecutionModel.TABLE, sqlQueryExecutionTable(expected));
    }
}
