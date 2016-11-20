package com.kwery.tests.dao.sqlquerydao;

import com.kwery.models.SqlQuery;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoListAllTest extends SqlQueryDaoListAbstractTest {
    @Test
    public void testGetAll() {
        List<SqlQuery> sqlQueries = sqlQueryDao.getAll();
        assertThat(sqlQueries, hasSize(3));
        assertThat(sqlQueries, hasItems(instanceOf(SqlQuery.class)));
    }
}
