package com.kwery.tests.dao.sqlquerydao;

import com.kwery.models.SqlQueryModel;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoListAllTest extends SqlQueryDaoListAbstractTest {
    @Test
    public void testGetAll() {
        List<SqlQueryModel> sqlQueries = sqlQueryDao.getAll();
        assertThat(sqlQueries, hasSize(3));
        assertThat(sqlQueries, hasItems(instanceOf(SqlQueryModel.class)));
    }
}
