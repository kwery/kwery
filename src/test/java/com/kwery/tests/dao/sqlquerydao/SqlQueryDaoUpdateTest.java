package com.kwery.tests.dao.sqlquerydao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.DatasourceDao;
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

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class SqlQueryDaoUpdateTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;

    protected SqlQuery sqlQuery1;
    protected SqlQuery sqlQuery2;

    protected List<String> emails = ImmutableList.of("foo@getkwery.com", "bar@getkwery.com", "goo@getkwery.com");

    protected Map<String, Integer> emailQueryIdMap = ImmutableMap.of(
            emails.get(0), 1,
            emails.get(1), 1,
            emails.get(2), 2
    );

    @Before
    public void setUpSqlQueryDaoUpdateTest() {
        sqlQuery1 = new SqlQuery();
        sqlQuery1.setId(1);
        sqlQuery1.setCronExpression("* * * * *");
        sqlQuery1.setLabel("testQuery0");
        sqlQuery1.setQuery("select * from foo");

        sqlQuery2 = new SqlQuery();
        sqlQuery2.setId(2);
        sqlQuery2.setCronExpression("* * * * *");
        sqlQuery2.setLabel("testQuery1");
        sqlQuery2.setQuery("select * from foo");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .values(2, "testDatasource1", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(sqlQuery1.getId(), sqlQuery1.getCronExpression(), sqlQuery1.getLabel(), sqlQuery1.getQuery(), 1)
                                .values(sqlQuery2.getId(), sqlQuery2.getCronExpression(), sqlQuery2.getLabel(), sqlQuery2.getQuery(), 1).build(),
                        insertInto(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                            .columns(SqlQuery.COLUMN_QUERY_RUN_ID_FK, SqlQuery.COLUMN_EMAIL)
                            .values(emailQueryIdMap.get(emails.get(0)), emails.get(0))
                            .values(emailQueryIdMap.get(emails.get(1)), emails.get(1))
                            .values(emailQueryIdMap.get(emails.get(2)), emails.get(2)).build()
                )
        ).launch();

        sqlQueryDao = getInstance(SqlQueryDao.class);

        DatasourceDao datasourceDao = getInstance(DatasourceDao.class);
        sqlQuery1.setDatasource(datasourceDao.getById(1));
        sqlQuery2.setDatasource(datasourceDao.getById(1));
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery expectedUpdated = new SqlQuery();

        SqlQuery fromDb = sqlQueryDao.getById(1);
        expectedUpdated.setId(1);

        fromDb.setQuery("select * from bar");
        expectedUpdated.setQuery(fromDb.getQuery());

        fromDb.setLabel("testQuery2");
        expectedUpdated.setLabel(fromDb.getLabel());

        fromDb.setCronExpression("*");
        expectedUpdated.setCronExpression(fromDb.getCronExpression());

        Datasource datasource = getInstance(DatasourceDao.class).getById(2);
        fromDb.setDatasource(datasource);
        expectedUpdated.setDatasource(datasource);

        sqlQueryDao.update(fromDb);

        List<SqlQuery> expectedQueries = ImmutableList.of(expectedUpdated, sqlQuery2);

        DataSetBuilder sqlQueryBuilder = new DataSetBuilder();
        for (SqlQuery q : expectedQueries) {
            sqlQueryBuilder.newRow(SqlQuery.TABLE)
                    .with(SqlQuery.COLUMN_ID, q.getId())
                    .with(SqlQuery.COLUMN_CRON_EXPRESSION, q.getCronExpression())
                    .with(SqlQuery.COLUMN_LABEL, q.getLabel())
                    .with(SqlQuery.COLUMN_QUERY, q.getQuery())
                    .with(SqlQuery.COLUMN_DATASOURCE_ID_FK, q.getDatasource().getId())
                    .add();
        }

        assertDbState(SqlQuery.TABLE, sqlQueryBuilder.build());


        DataSetBuilder emailBuilder = new DataSetBuilder();

        for (String s : emailQueryIdMap.keySet()) {
            emailBuilder.newRow(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                    .with(SqlQuery.COLUMN_QUERY_RUN_ID_FK, emailQueryIdMap.get(s))
                    .with(SqlQuery.COLUMN_EMAIL, s)
                    .add();
        }

        assertDbState(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT, emailBuilder.build());
    }

    @Test
    public void testUpdateToEmptyRecipientEmails() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery fromDb = sqlQueryDao.getById(1);
        fromDb.getRecipientEmails().clear();
        sqlQueryDao.update(fromDb);

        List<SqlQuery> expectedQueries = ImmutableList.of(sqlQuery1, sqlQuery2);

        DataSetBuilder sqlQueryBuilder = new DataSetBuilder();
        for (SqlQuery q : expectedQueries) {
            sqlQueryBuilder.newRow(SqlQuery.TABLE)
                    .with(SqlQuery.COLUMN_ID, q.getId())
                    .with(SqlQuery.COLUMN_CRON_EXPRESSION, q.getCronExpression())
                    .with(SqlQuery.COLUMN_LABEL, q.getLabel())
                    .with(SqlQuery.COLUMN_QUERY, q.getQuery())
                    .with(SqlQuery.COLUMN_DATASOURCE_ID_FK, q.getDatasource().getId())
                    .add();
        }

        assertDbState(SqlQuery.TABLE, sqlQueryBuilder.build());

        DataSetBuilder emailBuilder = new DataSetBuilder();
        for (String s : emailQueryIdMap.keySet()) {
            if (emailQueryIdMap.get(s).equals(1)) {
                continue;
            }

            emailBuilder.newRow(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                    .with(SqlQuery.COLUMN_QUERY_RUN_ID_FK, emailQueryIdMap.get(s))
                    .with(SqlQuery.COLUMN_EMAIL, s)
                    .add();
        }

        assertDbState(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT, emailBuilder.build());
    }

    @Test
    public void testUpdateToNewEmails() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery fromDb = sqlQueryDao.getById(1);
        fromDb.getRecipientEmails().clear();

        ImmutableSet<String> newEmails = ImmutableSet.of("purvi@getkwery.com", "pavi@getkwery.com");
        fromDb.getRecipientEmails().addAll(newEmails);
        sqlQueryDao.update(fromDb);

        List<SqlQuery> expectedQueries = ImmutableList.of(sqlQuery1, sqlQuery2);

        DataSetBuilder sqlQueryBuilder = new DataSetBuilder();
        for (SqlQuery q : expectedQueries) {
            sqlQueryBuilder.newRow(SqlQuery.TABLE)
                    .with(SqlQuery.COLUMN_ID, q.getId())
                    .with(SqlQuery.COLUMN_CRON_EXPRESSION, q.getCronExpression())
                    .with(SqlQuery.COLUMN_LABEL, q.getLabel())
                    .with(SqlQuery.COLUMN_QUERY, q.getQuery())
                    .with(SqlQuery.COLUMN_DATASOURCE_ID_FK, q.getDatasource().getId())
                    .add();
        }

        assertDbState(SqlQuery.TABLE, sqlQueryBuilder.build());

        DataSetBuilder emailBuilder = new DataSetBuilder();
        for (String s : emailQueryIdMap.keySet()) {
            if (emailQueryIdMap.get(s).equals(1)) {
                continue;
            }

            emailBuilder.newRow(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                    .with(SqlQuery.COLUMN_QUERY_RUN_ID_FK, emailQueryIdMap.get(s))
                    .with(SqlQuery.COLUMN_EMAIL, s)
                    .add();
        }


        for (String newEmail : newEmails) {
            emailBuilder.newRow(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                    .with(SqlQuery.COLUMN_QUERY_RUN_ID_FK, 1)
                    .with(SqlQuery.COLUMN_EMAIL, newEmail)
                    .add();
        }

        assertDbState(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT, emailBuilder.build());
    }

    @Test
    public void testRemoveSomeAndAddSomeEmails() throws DatabaseUnitException, SQLException, IOException {
        SqlQuery fromDb = sqlQueryDao.getById(1);
        fromDb.getRecipientEmails().remove("foo@getkwery.com");

        ImmutableSet<String> newEmails = ImmutableSet.of("purvi@getkwery.com", "pavi@getkwery.com");
        fromDb.getRecipientEmails().addAll(newEmails);

        sqlQueryDao.update(fromDb);

        List<SqlQuery> expectedQueries = ImmutableList.of(sqlQuery1, sqlQuery2);

        DataSetBuilder sqlQueryBuilder = new DataSetBuilder();
        for (SqlQuery q : expectedQueries) {
            sqlQueryBuilder.newRow(SqlQuery.TABLE)
                    .with(SqlQuery.COLUMN_ID, q.getId())
                    .with(SqlQuery.COLUMN_CRON_EXPRESSION, q.getCronExpression())
                    .with(SqlQuery.COLUMN_LABEL, q.getLabel())
                    .with(SqlQuery.COLUMN_QUERY, q.getQuery())
                    .with(SqlQuery.COLUMN_DATASOURCE_ID_FK, q.getDatasource().getId())
                    .add();
        }

        assertDbState(SqlQuery.TABLE, sqlQueryBuilder.build());

        DataSetBuilder emailBuilder = new DataSetBuilder();
        for (String s : emailQueryIdMap.keySet()) {
            if (emailQueryIdMap.get(s).equals(1)) {
                continue;
            }

            emailBuilder.newRow(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                    .with(SqlQuery.COLUMN_QUERY_RUN_ID_FK, emailQueryIdMap.get(s))
                    .with(SqlQuery.COLUMN_EMAIL, s)
                    .add();
        }

        Set<String> expectedEmails = new HashSet<>();
        expectedEmails.addAll(newEmails);
        expectedEmails.add("bar@getkwery.com");

        for (String newEmail : expectedEmails) {
            emailBuilder.newRow(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT)
                    .with(SqlQuery.COLUMN_QUERY_RUN_ID_FK, 1)
                    .with(SqlQuery.COLUMN_EMAIL, newEmail)
                    .add();
        }

        assertDbState(SqlQuery.TABLE_QUERY_RUN_EMAIL_RECIPIENT, emailBuilder.build());
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateLabelToExistingValue() {
        SqlQuery fromDb = sqlQueryDao.getById(1);
        fromDb.setLabel("testQuery1");
        sqlQueryDao.update(fromDb);
    }
}
