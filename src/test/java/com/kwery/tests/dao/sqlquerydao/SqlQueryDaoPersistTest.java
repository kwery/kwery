package com.kwery.tests.dao.sqlquerydao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.util.TestUtil.queryRun;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoPersistTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;

    protected Datasource datasource;

    @Before
    public void setUpQueryRunDaoPersisTest() {
        datasource = new Datasource();
        datasource.setId(1);
        datasource.setType(Datasource.Type.MYSQL);
        datasource.setLabel("mysql");
        datasource.setUrl("foo.com");
        datasource.setUsername("username");
        datasource.setPassword("password");
        datasource.setPort(3306);

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(Datasource.TABLE)
                        .row()
                        .column(Datasource.COLUMN_ID, datasource.getId())
                        .column(Datasource.COLUMN_TYPE, datasource.getType())
                        .column(Datasource.COLUMN_LABEL, datasource.getLabel())
                        .column(Datasource.COLUMN_URL, datasource.getUrl())
                        .column(Datasource.COLUMN_USERNAME, datasource.getUsername())
                        .column(Datasource.COLUMN_PASSWORD, datasource.getPassword())
                        .column(Datasource.COLUMN_PORT, datasource.getPort())
                        .end()
                        .build()
        ).launch();

        sqlQueryDao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void testPersist() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery q = queryRun();
        q.setDatasource(datasource);
        ImmutableSet<String> emails = ImmutableSet.of("foo@getkwery.com", "bar@getkwery.com");
        q.setRecipientEmails(emails);
        sqlQueryDao.save(q);
        assertThat(q.getId(), is(notNullValue()));


        DataSetBuilder sqlQueryBuilder = new DataSetBuilder();
        sqlQueryBuilder.newRow(SqlQuery.TABLE)
                .with(SqlQuery.COLUMN_ID, q.getId())
                .with(SqlQuery.COLUMN_CRON_EXPRESSION, q.getCronExpression())
                .with(SqlQuery.COLUMN_LABEL, q.getLabel())
                .with(SqlQuery.COLUMN_QUERY, q.getQuery())
                .with(SqlQuery.COLUMN_DATASOURCE_ID_FK, q.getDatasource().getId())
                .add();

        assertDbState(SqlQuery.TABLE, sqlQueryBuilder.build());

        DataSetBuilder emailBuilder = new DataSetBuilder();

        for (String email : emails) {
            emailBuilder.newRow(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                    .with(SqlQuery.COLUMN_QUERY_RUN_ID_FK, q.getId())
                    .with(SqlQuery.COLUMN_EMAIL, email)
                    .add();
        }

        assertDbState(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT, emailBuilder.build());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNullValuesValidation() {
        SqlQuery q = new SqlQuery();
        sqlQueryDao.save(q);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyValuesValidation() {
        SqlQuery q = new SqlQuery();
        q.setCronExpression("");
        q.setQuery("");
        q.setLabel("");
        sqlQueryDao.save(q);
    }
}
