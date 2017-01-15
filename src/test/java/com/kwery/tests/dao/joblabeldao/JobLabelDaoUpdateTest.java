package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.models.JobLabelModel.JOB_LABEL_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelTable;
import static com.kwery.tests.util.TestUtil.jobLabelModel;

public class JobLabelDaoUpdateTest extends RepoDashDaoTestBase {
    private JobLabelDao jobLabelDao;
    private JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() throws Exception {
        String label = UUID.randomUUID().toString();
        jobLabelModel.setLabel(label);
        jobLabelDao.save(jobLabelModel);

        new DbTableAsserterBuilder(JOB_LABEL_TABLE, jobLabelTable(jobLabelModel)).build().assertTable();
    }
}
