package com.kwery.tests.controllers.apis.integration.jobapicontroller.listjobs;

import com.google.common.collect.ImmutableSet;
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
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.exparity.hamcrest.BeanMatchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobApiControllerListJobsWithLabelIdTest extends AbstractPostLoginApiTest {
    private JobLabelModel jobLabelModel;
    private JobModel jobModel0;
    private JobModel jobModel1;
    private Map<String, JobModel> jobMap = new HashMap<>(2);

    @Before
    public void setUp() {
        jobModel0 = jobModelWithoutDependents();
        jobDbSetUp(jobModel0);

        jobModel1 = jobModelWithoutDependents();
        jobDbSetUp(jobModel1);

        JobModel jobModel2 = jobModelWithoutDependents();
        jobDbSetUp(jobModel2);

        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);

        jobModel0.setLabels(ImmutableSet.of(jobLabelModel));
        jobJobLabelDbSetUp(jobModel0);
        jobMap.put(jobModel0.getName(), jobModel0);

        jobModel1.setLabels(ImmutableSet.of(jobLabelModel));
        jobJobLabelDbSetUp(jobModel1);
        jobMap.put(jobModel1.getName(), jobModel1);

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
        filter.setJobLabelId(jobLabelModel.getId());
        filter.setPageNumber(0);
        filter.setResultCount(1);

        String response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobModelHackDtos.length()", is(1)));
        assertThat(response, hasJsonPath("$.totalCount", is(2)));

        JobModelHackDto jobModelHackDto0 = JsonPath.parse(response).read("$.jobModelHackDtos[0]", JobModelHackDto.class);
        assertThat(jobModelHackDto0.getJobModel(), BeanMatchers.theSameBeanAs(jobMap.get(jobModelHackDto0.getJobModel().getName())));

        filter.setPageNumber(1);

        response = ninjaTestBrowser.postJson(getUrl(url), filter);
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobModelHackDtos.length()", is(1)));
        assertThat(response, hasJsonPath("$.totalCount", is(2)));

        JobModelHackDto jobModelHackDto1 = JsonPath.parse(response).read("$.jobModelHackDtos[0]", JobModelHackDto.class);
        assertThat(jobModelHackDto1.getJobModel(), BeanMatchers.theSameBeanAs(jobMap.get(jobModelHackDto1.getJobModel().getName())));
    }
}
