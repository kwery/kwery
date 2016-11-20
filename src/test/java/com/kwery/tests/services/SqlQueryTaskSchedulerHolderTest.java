package com.kwery.tests.services;

import com.kwery.services.scheduler.SqlQueryTaskScheduler;
import com.kwery.services.scheduler.SqlQueryTaskSchedulerHolder;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class SqlQueryTaskSchedulerHolderTest {
    @Test
    public void test() {
        SqlQueryTaskSchedulerHolder sqlQueryTaskSchedulerHolder = new SqlQueryTaskSchedulerHolder();
        SqlQueryTaskScheduler scheduler0 = mock(SqlQueryTaskScheduler.class);
        SqlQueryTaskScheduler scheduler1 = mock(SqlQueryTaskScheduler.class);
        SqlQueryTaskScheduler scheduler2 = mock(SqlQueryTaskScheduler.class);

        assertThat(sqlQueryTaskSchedulerHolder.add(1, scheduler0), is(true));
        assertThat(sqlQueryTaskSchedulerHolder.add(1, scheduler1), is(true));
        assertThat(sqlQueryTaskSchedulerHolder.add(2, scheduler2), is(true));

        Collection<SqlQueryTaskScheduler> list = sqlQueryTaskSchedulerHolder.get(1);

        assertThat(list, hasSize(2));

        assertThat(list, containsInAnyOrder(scheduler0, scheduler1));

        Collection<SqlQueryTaskScheduler> single = sqlQueryTaskSchedulerHolder.get(2);

        assertThat(single, hasSize(1));

        assertThat(single, containsInAnyOrder(scheduler2));

        sqlQueryTaskSchedulerHolder.remove(1, scheduler0);

        Collection<SqlQueryTaskScheduler> listPostRemoval = sqlQueryTaskSchedulerHolder.get(1);

        assertThat(listPostRemoval, hasSize(1));

        assertThat(listPostRemoval, containsInAnyOrder(scheduler1));

        sqlQueryTaskSchedulerHolder.remove(1, scheduler1);

        assertThat(sqlQueryTaskSchedulerHolder.get(1), empty());

        sqlQueryTaskSchedulerHolder.remove(2, scheduler2);

        assertThat(sqlQueryTaskSchedulerHolder.get(2), empty());

        sqlQueryTaskSchedulerHolder.add(1, scheduler0);
        sqlQueryTaskSchedulerHolder.add(1, scheduler1);

        assertThat(sqlQueryTaskSchedulerHolder.remove(1), is(true));

        assertThat(sqlQueryTaskSchedulerHolder.get(1), empty());

        sqlQueryTaskSchedulerHolder.add(1, scheduler0);
        sqlQueryTaskSchedulerHolder.add(1, scheduler1);
        sqlQueryTaskSchedulerHolder.add(2, scheduler2);

        assertThat(sqlQueryTaskSchedulerHolder.all(), hasSize(3));
        assertThat(sqlQueryTaskSchedulerHolder.all(), containsInAnyOrder(scheduler0, scheduler1, scheduler2));
    }
}
