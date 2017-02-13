package com.kwery.tests.dao.jobexecutiondao;

import com.kwery.dao.JobExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.kwery.models.JobExecutionModel.TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobExecutionModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;

public class JobExecutionDaoDeleteJobExecutionTest extends RepoDashDaoTestBase {
    public List<Integer> ids = new ArrayList<>(3);
    private JobExecutionDao jobExecutionDao;
    protected JobExecutionModel last;

    @Before
    public void setUp() {
        Datasource datasource = TestUtil.datasource();
        DbUtil.datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = TestUtil.sqlQueryModel(datasource);
        DbUtil.sqlQueryDbSetUp(sqlQueryModel);

        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        for (int i = 0; i < 3; ++i) {
            last = jobExecutionModel();
            last.setJobModel(jobModel);
            jobExecutionDbSetUp(last);
            ids.add(last.getId());

            SqlQueryExecutionModel sqlQueryExecutionModel = TestUtil.sqlQueryExecutionModel();
            sqlQueryExecutionModel.setSqlQuery(sqlQueryModel);
            sqlQueryExecutionModel.setJobExecutionModel(last);
            DbUtil.sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);
        }

        jobExecutionDao = getInstance(JobExecutionDao.class);
    }

    @Test
    public void test() throws Exception {
        List<Integer> toDeleteIds = ids.subList(0, ids.size() - 1);
        jobExecutionDao.deleteJobExecutions(toDeleteIds);
        new DbTableAsserterBuilder(TABLE, jobExecutionTable(last)).build().assertTable();
    }
}
