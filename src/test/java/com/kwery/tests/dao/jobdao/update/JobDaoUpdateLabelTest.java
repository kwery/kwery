package com.kwery.tests.dao.jobdao.update;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.dozer.DozerBeanMapper;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobLabelModel.JOB_LABEL_TABLE;
import static com.kwery.models.JobModel.JOB_JOB_LABEL_TABLE;
import static com.kwery.models.JobModel.JOB_JOB_LABEL_TABLE_ID_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobDaoUpdateLabelTest extends RepoDashDaoTestBase {
    private JobDao jobDao;
    private JobModel jobModel;
    private JobLabelModel jobLabelModel1;
    private JobLabelModel jobLabelModel2;
    private JobLabelModel jobLabelModel0;
    private long created;

    @Before
    public void setUp() {
        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        jobLabelModel2 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel2);

        jobModel = jobModelWithoutDependents();
        jobModel.setParameterCsv("foo bar moo");
        jobModel.getLabels().addAll(ImmutableSet.of(jobLabelModel0, jobLabelModel2));
        jobDbSetUp(jobModel);

        created = jobModel.getCreated();

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testClearLabel() throws Exception {
        jobModel.getLabels().clear();

        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);

        long now = System.currentTimeMillis();

        TestUtil.nullifyTimestamps(jobModel);
        jobModel = jobDao.save(jobModel);
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserterBuilder(JOB_JOB_LABEL_TABLE, jobJobLabelTable(expected)).columnToIgnore(JOB_JOB_LABEL_TABLE_ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JOB_LABEL_TABLE, jobLabelTable(jobLabelModel0, jobLabelModel1, jobLabelModel2)).build().assertTable();

        assertThat(jobModel.getUpdated(), greaterThanOrEqualTo(now));
        assertThat(jobModel.getCreated(), is(created));
    }

    @Test
    public void testAddAndDeleteLabel() throws Exception {
        jobModel.getLabels().remove(jobLabelModel1);
        jobModel.getLabels().add(jobLabelModel2);

        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);

        long now = System.currentTimeMillis();

        TestUtil.nullifyTimestamps(jobModel);
        jobModel = jobDao.save(jobModel);
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserterBuilder(JOB_JOB_LABEL_TABLE, jobJobLabelTable(expected)).columnToIgnore(JOB_JOB_LABEL_TABLE_ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JOB_LABEL_TABLE, jobLabelTable(jobLabelModel0, jobLabelModel1, jobLabelModel2)).build().assertTable();

        assertThat(jobModel.getUpdated(), greaterThanOrEqualTo(now));
        assertThat(jobModel.getCreated(), is(created));
    }
}
