package com.kwery.tests.utils;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.utils.KweryUtil;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class KweryUtilsAllJobLabelIdsTest extends RepoDashDaoTestBase {
    private JobLabelDao jobLabelDao;
    private List<Integer> expectedIds;
    private JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        jobLabelDao = getInstance(JobLabelDao.class);
        expectedIds = new LinkedList<>();

        jobLabelModel = TestUtil.jobLabelModel();
        jobLabelModel.setId(RandomUtils.nextInt());
        DbUtil.jobLabelDbSetUp(jobLabelModel);
        expectedIds.add(jobLabelModel.getId());

        for (int i = 0; i < 3; ++i) {
            setUpJobLabelHierarchy(expectedIds, jobLabelModel);
        }
    }

    private void setUpJobLabelHierarchy(List<Integer> expectedIds, JobLabelModel root) {
        JobLabelModel jobLabelModel = jobLabelModel();
        jobLabelModel.setId(RandomUtils.nextInt());
        jobLabelModel.setParentLabel(root);
        jobLabelDbSetUp(jobLabelModel);
        expectedIds.add(jobLabelModel.getId());

        JobLabelModel parent = jobLabelModel;
        for (int i = 0; i < 3; ++i) {
            JobLabelModel childJobLabelModel = jobLabelModel();
            childJobLabelModel.setId(RandomUtils.nextInt());
            childJobLabelModel.setParentLabel(parent);
            jobLabelDbSetUp(childJobLabelModel);
            parent = childJobLabelModel;
            expectedIds.add(childJobLabelModel.getId());
        }
    }

    @Test
    public void test() {
        JobLabelModel fromDb = jobLabelDao.getJobLabelModelById(jobLabelModel.getId());
        assertThat(KweryUtil.allJobLabelIds(fromDb), containsInAnyOrder(expectedIds.toArray(new Integer[13])));
    }
}
