package com.kwery.tests.services.search;

import com.kwery.dao.search.JobSearchDao;
import com.kwery.dao.search.SearchFilter;
import com.kwery.models.JobModel;
import com.kwery.services.search.SearchIndexer;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SearchIndexerTest extends RepoDashTestBase {
    private JobSearchDao jobSearchDao;
    private JobModel jobModel;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        getInstance(SearchIndexer.class).index();

        jobSearchDao = getInstance(JobSearchDao.class);
    }

    @Test
    public void test() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(jobModel.getTitle());
        searchFilter.setMaxResults(1);

        searchFilter.setFirstResult(0);
        List result = jobSearchDao.search(searchFilter);

        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), is(jobModel.getId()));
    }
}
