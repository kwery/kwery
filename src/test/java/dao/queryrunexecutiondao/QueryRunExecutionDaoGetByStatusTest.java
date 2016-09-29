package dao.queryrunexecutiondao;

import dao.QueryRunExecutionDao;
import models.QueryRunExecution;
import models.QueryRunExecution.Status;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static util.TestUtil.queryRunExecution;

public class QueryRunExecutionDaoGetByStatusTest extends RepoDashDaoTestBase {
    private QueryRunExecutionDao queryRunExecutionDao;
    Map<Status, QueryRunExecution> map = new HashMap<>();

    @Before
    public void setUpQueryRunExecutionDaoGetByStatusTest() {
        queryRunExecutionDao = getInstance(QueryRunExecutionDao.class);

        for (Status status : Status.values()) {
            QueryRunExecution e = queryRunExecution(status);
            map.put(status, e);
            queryRunExecutionDao.save(e);
        }
    }

    @Test
    public void test() {
        for (Status status : Status.values()) {
            List<QueryRunExecution> queryRunExecutions = queryRunExecutionDao.getByStatus(status);
            assertThat(queryRunExecutions, hasSize(1));
            assertThat(queryRunExecutions.get(0), samePropertyValuesAs(map.get(status)));
        }
    }
}
