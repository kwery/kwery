package com.kwery.tests.dao.sqlquerydao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static junit.framework.TestCase.fail;

public class SqlQueryDaoUpdateTest extends RepoDashDaoTestBase {
    protected SqlQueryDao sqlQueryDao;

    protected SqlQueryModel sqlQuery1;
    protected SqlQueryModel sqlQuery2;

    protected int datasourceId0 = 1;

    @Before
    public void setUpSqlQueryDaoUpdateTest() {
        sqlQuery1 = new SqlQueryModel();
        sqlQuery1.setId(1);
        sqlQuery1.setLabel("testQuery0");
        sqlQuery1.setQuery("select * from foo");

        sqlQuery2 = new SqlQueryModel();
        sqlQuery2.setId(2);
        sqlQuery2.setLabel("testQuery1");
        sqlQuery2.setQuery("select * from foo");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasourceId0, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "foo")
                                .values(2, "testDatasource1", "password", 3306, MYSQL.name(), "foo.com", "foo").build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(sqlQuery1.getId(), sqlQuery1.getLabel(), sqlQuery1.getQuery(), 1)
                                .values(sqlQuery2.getId(), sqlQuery2.getLabel(), sqlQuery2.getQuery(), 1).build()
                )
        ).launch();

        sqlQueryDao = getInstance(SqlQueryDao.class);

        DatasourceDao datasourceDao = getInstance(DatasourceDao.class);
        sqlQuery1.setDatasource(datasourceDao.getById(1));
        sqlQuery2.setDatasource(datasourceDao.getById(1));
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SqlQueryModel expectedUpdated = new SqlQueryModel();

        SqlQueryModel fromDb = sqlQueryDao.getById(1);
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

        sqlQueryDao.save(fromDb);

        List<SqlQueryModel> expectedQueries = ImmutableList.of(expectedUpdated, sqlQuery2);

        DataSetBuilder sqlQueryBuilder = new DataSetBuilder();
        for (SqlQueryModel q : expectedQueries) {
            sqlQueryBuilder.newRow(SqlQueryModel.SQL_QUERY_TABLE)
                    .with(SqlQueryModel.ID_COLUMN, q.getId())
                    .with(SqlQueryModel.LABEL_COLUMN, q.getLabel())
                    .with(SqlQueryModel.QUERY_COLUMN, q.getQuery())
                    .with(SqlQueryModel.DATASOURCE_ID_FK_COLUMN, q.getDatasource().getId())
                    .add();
        }

        assertDbState(SqlQueryModel.SQL_QUERY_TABLE, sqlQueryBuilder.build());
    }

    @Test
    public void testUniqueLabelConstraintTest() {
        try {
            sqlQuery1.setId(null);
            sqlQueryDao.save(sqlQuery1);
        } catch (PersistenceException e) {
            if (!(e.getCause() instanceof ConstraintViolationException)) {
                fail();
            }
        }
    }
}
