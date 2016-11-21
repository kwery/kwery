package com.kwery.tests.dao.sqlquerydao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.exparity.hamcrest.BeanMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.UUID;

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
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryDao dao;

    protected SqlQuery selectSqlQuery;
    protected SqlQuery dependentSelectSqlQuery;
    protected SqlQuery dependentSleepQuery;

    @Before
    public void setUpQueryRunDaoQueryTest() {
        Datasource datasource = new Datasource();
        datasource.setId(1);
        datasource.setLabel("testDatasource");
        datasource.setPassword("password");
        datasource.setUsername("username");
        datasource.setPort(3306);
        datasource.setUrl("foo.com");
        datasource.setType(MYSQL);

        selectSqlQuery = new SqlQuery();
        selectSqlQuery.setId(1);
        selectSqlQuery.setCronExpression("* * * * *");
        selectSqlQuery.setQuery("select * from mysql.db");
        selectSqlQuery.setDatasource(datasource);
        selectSqlQuery.setLabel("selectQuery");

        dependentSelectSqlQuery = new SqlQuery();
        dependentSelectSqlQuery.setId(2);
        dependentSelectSqlQuery.setCronExpression("* * * * *");
        dependentSelectSqlQuery.setQuery("select * from mysql.db");
        dependentSelectSqlQuery.setDatasource(datasource);
        dependentSelectSqlQuery.setLabel("dependentSelectQuery");
        dependentSelectSqlQuery.setDependentQueries(new LinkedList<>());

        dependentSleepQuery = new SqlQuery();
        dependentSleepQuery.setId(3);
        dependentSleepQuery.setCronExpression("* * * * *");
        dependentSleepQuery.setQuery("select * from mysql.db");
        dependentSleepQuery.setDatasource(datasource);
        dependentSleepQuery.setLabel("dependentSleepQuery");
        dependentSleepQuery.setDependentQueries(new LinkedList<>());

        selectSqlQuery.setDependentQueries(ImmutableList.of(dependentSelectSqlQuery, dependentSleepQuery));

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(),
                                        datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
                                .values(selectSqlQuery.getId(), selectSqlQuery.getCronExpression(), selectSqlQuery.getLabel(), selectSqlQuery.getQuery(),
                                        selectSqlQuery.getDatasource().getId())
                                .values(dependentSelectSqlQuery.getId(), dependentSelectSqlQuery.getCronExpression(), dependentSelectSqlQuery.getLabel(),
                                        dependentSelectSqlQuery.getQuery(), dependentSelectSqlQuery.getDatasource().getId())
                                .values(dependentSleepQuery.getId(), dependentSleepQuery.getCronExpression(), dependentSleepQuery.getLabel(),
                                        dependentSleepQuery.getQuery(), dependentSleepQuery.getDatasource().getId())
                                .build(),
                        insertInto(SqlQuery.TABLE_QUERY_RUN_DEPENDENT)
                                .columns(SqlQuery.COLUMN_QUERY_RUN_ID_FK, SqlQuery.COLUMN_DEPENDENT_QUERY_RUN_ID_FK)
                                .values(1, 2)
                                .values(1, 3)
                                .build()
                )
        ).launch();

        dao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void testGetByLabel() {
        assertThat(dao.getByLabel(selectSqlQuery.getLabel()), BeanMatchers.theSameAs(selectSqlQuery));
        assertThat(dao.getByLabel(UUID.randomUUID().toString()), nullValue());
    }

    @Test
    public void testGetById() {
        assertThat(dao.getById(selectSqlQuery.getId()), BeanMatchers.theSameAs(selectSqlQuery));
        assertThat(dao.getById(Integer.MAX_VALUE), nullValue());
    }
}
