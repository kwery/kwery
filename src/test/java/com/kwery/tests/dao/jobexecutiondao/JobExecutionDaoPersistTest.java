package com.kwery.tests.dao.jobexecutiondao;

import com.kwery.dao.JobExecutionDao;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobExecutionModelWithoutId;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;

public class JobExecutionDaoPersistTest extends RepoDashDaoTestBase {
    protected JobExecutionDao jobExecutionDao;
    protected JobModel jobModel;

    @Before
    public void setUpQueryRunExecutionDaoTest() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);
        jobExecutionDao = getInstance(JobExecutionDao.class);
    }

    @Test
    public void testPersist() throws DatabaseUnitException, SQLException, IOException {
        JobExecutionModel jobExecutionModel = jobExecutionModelWithoutId();
        jobExecutionModel.setJobModel(jobModel);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobExecutionModel expected = mapper.map(jobExecutionModel, JobExecutionModel.class);

        jobExecutionDao.save(jobExecutionModel);

        expected.setId(jobExecutionModel.getId());

        assertDbState(JobExecutionModel.TABLE, jobExecutionTable(expected));
    }
}
