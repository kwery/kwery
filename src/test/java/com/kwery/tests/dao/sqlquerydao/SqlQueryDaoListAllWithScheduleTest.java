package com.kwery.tests.dao.sqlquerydao;

import com.kwery.models.SqlQuery;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoListAllWithScheduleTest extends SqlQueryDaoListAbstractTest {
    @Test
    public void testGetAll() {
        List<SqlQuery> sqlQueries = sqlQueryDao.getAllWithSchedule();
        assertThat(sqlQueries, hasSize(1));
        assertThat(sqlQueries.get(0).getId(), is(1));
        assertThat(sqlQueries, hasItems(instanceOf(SqlQuery.class)));
    }
}
