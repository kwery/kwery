package com.kwery.tests.dao.jobdao;

import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobDaoGetAllJobsTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;
    protected Datasource datasource;

    protected List<JobModel> jobModels;
    protected Map<Integer, JobModel> idJobModelMap;

    @Before
    public void setUpJobDaoUpdateTest() {
        jobModels = new ArrayList<>(2);
        idJobModelMap = new HashMap<>(2);

        for (int i = 0; i < 2; ++i) {
            jobModel = jobModelWithoutDependents();
            jobModels.add(jobModel);

            idJobModelMap.put(jobModel.getId(), jobModel);
        }

        DbUtil.jobDbSetUp(jobModels);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() {
        List<JobModel> jobModels = jobDao.getAllJobs();
        for (JobModel model : jobModels) {
            assertThat(model, theSameBeanAs(idJobModelMap.get(model.getId())));
        }
    }
}
