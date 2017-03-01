package com.kwery.tests.controllers.apis.integration.jobapicontroller.search;

import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.JobDao;
import com.kwery.dao.search.SearchFilter;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.assertJsonJobModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;

public class SearchApiControllerJobSearchTest extends AbstractPostLoginApiTest {
    private JobModel jobModel0;
    private JobApiController jobApiController;
    private JobModel jobModel1;

    @Before
    public void setUp() {
        JobDao jobDao = getInjector().getInstance(JobDao.class);

        jobModel0 = jobModelWithoutIdWithoutDependents();
        jobModel0.setTitle("foo");
        jobDao.save(jobModel0);

        jobModel1 = jobModelWithoutIdWithoutDependents();
        jobModel1.setName("foo");
        jobDao.save(jobModel1);

        jobApiController = getInjector().getInstance(JobApiController.class);
    }

    @Test
    public void test() {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "searchJobs");
        SearchFilter searchFilter = new SearchFilter();

        searchFilter.setPhrase("foo");
        searchFilter.setFirstResult(0);
        searchFilter.setMaxResults(1);

        String response = ninjaTestBrowser.postJson(getUrl(url), searchFilter);

        assertJsonJobModel(response, 0, jobApiController.toJobModelHackDto(jobModel0));

        searchFilter.setPhrase("foo");
        searchFilter.setFirstResult(1);
        searchFilter.setMaxResults(1);

        response = ninjaTestBrowser.postJson(getUrl(url), searchFilter);

        assertJsonJobModel(response, 0, jobApiController.toJobModelHackDto(jobModel1));
    }
}
