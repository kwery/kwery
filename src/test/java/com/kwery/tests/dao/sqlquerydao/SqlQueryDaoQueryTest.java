package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.exparity.hamcrest.BeanMatchers.theSameAs;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryDao dao;

    protected SqlQueryModel selectSqlQuery;

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

        selectSqlQuery = new SqlQueryModel();
        selectSqlQuery.setId(1);
        selectSqlQuery.setQuery("select * from mysql.db");
        selectSqlQuery.setDatasource(datasource);
        selectSqlQuery.setLabel("selectQuery");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(),
                                        datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(selectSqlQuery.getId(), selectSqlQuery.getLabel(), selectSqlQuery.getQuery(),
                                        selectSqlQuery.getDatasource().getId())
                                .build()
                )
        ).launch();

        dao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void testGetByLabel() {
        assertThat(dao.getByLabel(selectSqlQuery.getLabel()), theSameAs(selectSqlQuery));
        assertThat(dao.getByLabel(UUID.randomUUID().toString()), nullValue());
    }

    @Test
    public void testGetById() {
        assertThat(dao.getById(selectSqlQuery.getId()), theSameAs(selectSqlQuery));
        assertThat(dao.getById(Integer.MAX_VALUE), nullValue());
    }
}
