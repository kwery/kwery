package com.kwery.tests.dao.jobdao;

import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryModel.ID_COLUMN;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class JobDaoUpdateTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;
    protected Datasource datasource;

    @Before
    public void setUpJobDaoUpdateTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(new HashSet<>());

        datasource = datasource();

        for (int i = 0; i < 2; ++i) {
            jobModel.getSqlQueries().add(sqlQueryModel(datasource));
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

        jobDbSetUp(jobModel);

        sqlQueryDbSetUp(jobModel.getSqlQueries());

        int id = 0;
        for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.sequenceOf(
                            Operations.insertInto(JOB_SQL_QUERY_TABLE)
                                    .row()
                                    .column(JobModel.ID_COLUMN, ++id)
                                    .column(JobModel.JOB_ID_FK_COLUMN, jobModel.getId())
                                    .column(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                                    .end()
                                    .build()
                    )
            ).launch();
        }

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        JobModel modifiedJobModel = jobModelWithoutIdWithoutDependents();
        modifiedJobModel.setSqlQueries(new HashSet<>());
        modifiedJobModel.getSqlQueries().addAll(jobModel.getSqlQueries());

        modifiedJobModel.setId(jobModel.getId());

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expectedJobModel = mapper.map(modifiedJobModel, JobModel.class);

        jobDao.save(modifiedJobModel);

        assertDbState(JOB_TABLE, jobTable(expectedJobModel));
        assertDbState(SQL_QUERY_TABLE, sqlQueryTable(expectedJobModel.getSqlQueries()));
        assertDbState(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(expectedJobModel), ID_COLUMN);
    }

    @Test
    public void testAddSqlQuery() throws DatabaseUnitException, SQLException, IOException {
        SqlQueryModel sqlQueryModel = sqlQueryModelWithoutId();
        sqlQueryModel.setDatasource(datasource);
        jobModel.getSqlQueries().add(sqlQueryModel);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expectedJobModel = mapper.map(jobModel, JobModel.class);

        JobModel fromDbJobModel = jobDao.save(jobModel);

        assertDbState(JOB_TABLE, jobTable(expectedJobModel));
        assertDbState(SQL_QUERY_TABLE, sqlQueryTable(expectedJobModel.getSqlQueries()), ID_COLUMN);
        assertDbState(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(expectedJobModel), ID_COLUMN, SQL_QUERY_ID_FK_COLUMN);

        for (SqlQueryModel queryModel : fromDbJobModel.getSqlQueries()) {
            Assert.assertThat(queryModel.getId(), Matchers.greaterThan(0));
        }
    }

    @Test
    public void testRemoveSqlQuery() throws DatabaseUnitException, SQLException, IOException {
        Iterator<SqlQueryModel> sqlQueryModelIterator = jobModel.getSqlQueries().iterator();

        if (sqlQueryModelIterator.hasNext()) {
            sqlQueryModelIterator.next();
            sqlQueryModelIterator.remove();
        }

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expectedJobModel = mapper.map(jobModel, JobModel.class);

        jobDao.save(jobModel);

        assertDbState(JOB_TABLE, jobTable(expectedJobModel));
        assertDbState(SQL_QUERY_TABLE, sqlQueryTable(expectedJobModel.getSqlQueries()));
        assertDbState(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(expectedJobModel), ID_COLUMN);
    }


    @Test
    public void testUpdateSqlQuery() throws DatabaseUnitException, SQLException, IOException {
        Iterator<SqlQueryModel> sqlQueryModelIterator = jobModel.getSqlQueries().iterator();

        if (sqlQueryModelIterator.hasNext()) {
            SqlQueryModel sqlQueryModel = sqlQueryModelIterator.next();

            sqlQueryModel.setLabel("foobarmoo");
            sqlQueryModel.setQuery("select * from foo");
        }

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expectedJobModel = mapper.map(jobModel, JobModel.class);
        jobDao.save(jobModel);

        DbTableAsserter jobTableAsserter = new DbTableAsserterBuilder(JOB_TABLE, jobTable(expectedJobModel))
                .build();
        jobTableAsserter.assertTable();

        DbTableAsserter sqlQueryTableAsserter = new DbTableAsserterBuilder(SQL_QUERY_TABLE, sqlQueryTable(expectedJobModel.getSqlQueries()))
                .columnToCompare(SqlQueryModel.ID_COLUMN)
                .build();
        sqlQueryTableAsserter.assertTable();

        DbTableAsserter jobSqlQueryTableAsserter = new DbTableAsserterBuilder(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(expectedJobModel))
                .columnsToCompare(JobModel.JOB_ID_FK_COLUMN, JobModel.SQL_QUERY_ID_FK_COLUMN)
                .columnToIgnore("id")
                .build();
        jobSqlQueryTableAsserter.assertTable();

    }
}
