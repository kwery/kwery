package com.kwery.tests.dao.search;

import com.kwery.dao.JobDao;
import com.kwery.dao.search.JobSearchDao;
import com.kwery.dao.search.SearchFilter;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SearchDaoTestPhrasePrecedence extends RepoDashDaoTestBase {
    private JobSearchDao jobSearchDao;
    private JobModel jobModel0;
    private JobModel jobModel1;

    @Before
    public void setUp() {
        JobDao jobDao = getInstance(JobDao.class);

        jobModel0 = jobModelWithoutIdWithoutDependents();
        jobModel0.setTitle("foo goo moo");
        jobDao.save(jobModel0);

        jobModel1 = jobModelWithoutIdWithoutDependents();
        jobModel1.setName("foo bar moo");
        jobDao.save(jobModel1);

        jobSearchDao = getInstance(JobSearchDao.class);
    }

    @Test
    public void test() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase("foo bar");
        searchFilter.setMaxResults(1);

        searchFilter.setFirstResult(0);
        List result = jobSearchDao.search(searchFilter);

        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), is(jobModel1.getId()));


        searchFilter.setFirstResult(1);
        result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), is(jobModel0.getId()));
    }
}
