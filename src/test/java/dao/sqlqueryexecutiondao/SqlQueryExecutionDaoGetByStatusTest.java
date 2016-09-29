package dao.sqlqueryexecutiondao;

import dao.SqlQueryExecutionDao;
import models.SqlQueryExecution;
import models.SqlQueryExecution.Status;
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

public class SqlQueryExecutionDaoGetByStatusTest extends RepoDashDaoTestBase {
    private SqlQueryExecutionDao sqlQueryExecutionDao;
    Map<Status, SqlQueryExecution> map = new HashMap<>();

    @Before
    public void setUpQueryRunExecutionDaoGetByStatusTest() {
        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);

        for (Status status : Status.values()) {
            SqlQueryExecution e = queryRunExecution(status);
            map.put(status, e);
            sqlQueryExecutionDao.save(e);
        }
    }

    @Test
    public void test() {
        for (Status status : Status.values()) {
            List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.getByStatus(status);
            assertThat(sqlQueryExecutions, hasSize(1));
            assertThat(sqlQueryExecutions.get(0), samePropertyValuesAs(map.get(status)));
        }
    }
}
