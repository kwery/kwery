package com.kwery.tests.controllers.apis.integration.jobapicontroller.listjobs;

import com.google.common.collect.ImmutableList;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dtos.JobListFilterDto;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDependentDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerListJobsTest extends AbstractPostLoginApiTest {
    JobModel jobModel;
    JobModel dependentJobModel;

    Map<String, JobModel> jobMap = new HashMap<>(2);

    @Before
    public void setUpJobApiControllerGetAllJobsTest() {
        jobModel = jobModelWithoutDependents();
        dependentJobModel = jobModelWithoutDependents();

        jobDbSetUp(ImmutableList.of(jobModel, dependentJobModel));

        jobModel.getChildJobs().add(dependentJobModel);

        jobDependentDbSetUp(jobModel);

        dependentJobModel.setParentJob(jobModel);

        jobMap.put(jobModel.getName(), jobModel);
        jobMap.put(dependentJobModel.getName(), dependentJobModel);

        //For JSON deserialisation
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobs");

        JobListFilterDto filter = new JobListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(1);

        asserts(ninjaTestBrowser.postJson(getUrl(url), filter));

        filter.setPageNumber(1);

        asserts(ninjaTestBrowser.postJson(getUrl(url), filter));
    }

    public void asserts(String response) {
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobModelHackDtos.length()", is(1)));
        assertThat(response, hasJsonPath("$.totalCount", is(2)));

        JobModelHackDto jobModelHackDto = JsonPath.parse(response).read("$.jobModelHackDtos[0]", JobModelHackDto.class);
        JobModel expectedJobModel = jobMap.remove(jobModelHackDto.getJobModel().getName());

        assertThat(jobModelHackDto, theSameBeanAs(new JobModelHackDto(expectedJobModel, expectedJobModel.getParentJob()))
                .excludePath("JobModelHackDto.JobModel.ChildJobs.ParentJob")
                .excludePath("JobModelHackDto.ParentJobModel.ChildJobs")
                .excludePath("JobModelHackDto.JobModel.ParentJob"));
    }
}
