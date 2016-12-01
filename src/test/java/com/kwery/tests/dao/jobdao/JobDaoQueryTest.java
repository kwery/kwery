package com.kwery.tests.dao.jobdao;

import org.junit.Test;

import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobDaoQueryTest extends JobDaoUpdateTest {
    @Test
    public void testGetJobById() {
        assertThat(jobDao.getJobById(jobModel.getId()), theSameBeanAs(jobModel));
        assertThat(jobDao.getJobById(jobModel.getId() + 1), nullValue());
    }
}
