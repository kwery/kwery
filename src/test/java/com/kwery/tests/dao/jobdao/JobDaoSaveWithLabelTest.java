package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobLabelModel.JOB_LABEL_TABLE;
import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobDaoSaveWithLabelTest extends RepoDashDaoTestBase {
    private JobLabelModel jobLabelModel0;
    private JobLabelModel jobLabelModel1;

    @Before
    public void setUp() {
        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);
    }

    @Test
    public void test() throws Exception {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        jobModel.setLabels(ImmutableSet.of(jobLabelModel0, jobLabelModel1));

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(jobModel, JobModel.class);

        jobModel = getInstance(JobDao.class).save(jobModel);
        expected.setId(jobModel.getId());
        expected.setCreated(jobModel.getCreated());
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserterBuilder(JOB_TABLE, jobTable(expected)).build().assertTable();
        new DbTableAsserterBuilder(JOB_JOB_LABEL_TABLE, jobJobLabelTable(expected)).columnToIgnore(JOB_JOB_LABEL_TABLE_ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JOB_LABEL_TABLE, jobLabelTable(jobLabelModel0, jobLabelModel1)).build().assertTable();

        assertThat(expected.getCreated(), notNullValue());
        assertThat(expected.getUpdated(), nullValue());
    }
}
