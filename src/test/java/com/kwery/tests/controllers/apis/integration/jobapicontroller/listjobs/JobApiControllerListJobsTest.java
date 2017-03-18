package com.kwery.tests.controllers.apis.integration.jobapicontroller.listjobs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.Configuration;
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
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class JobApiControllerListJobsTest extends AbstractPostLoginApiTest {
    JobModel jobModel;
    JobModel dependentJobModel;
    private JobApiController jobApiController;
    private List<JobModel> models;

    @Before
    public void setUpJobApiControllerGetAllJobsTest() {
        jobModel = jobModelWithoutDependents();
        dependentJobModel = jobModelWithoutDependents();

        jobDbSetUp(ImmutableList.of(jobModel, dependentJobModel));

        jobModel.getChildJobs().add(dependentJobModel);

        jobDependentDbSetUp(jobModel);

        dependentJobModel.setParentJob(jobModel);

        models = Lists.newArrayList(jobModel, dependentJobModel);
        models.sort(Comparator.comparing(JobModel::getUpdated).reversed());

        JobLabelModel jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        JobLabelModel jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        JobLabelModel jobLabelModel2 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel2);

        jobModel.getLabels().add(jobLabelModel0);
        jobModel.getLabels().add(jobLabelModel1);

        jobJobLabelDbSetUp(jobModel);

        dependentJobModel.getLabels().add(jobLabelModel2);

        jobJobLabelDbSetUp(dependentJobModel);

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

        jobApiController = getInjector().getInstance(JobApiController.class);
    }

    @Test
    public void test() throws JSONException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(JobApiController.class, "listJobs");

        JobListFilterDto filter = new JobListFilterDto();
        filter.setPageNumber(0);
        filter.setResultCount(1);

        assertResponse(ninjaTestBrowser.postJson(getUrl(url), filter), jobApiController.toJobModelHackDto(models.get(0)));

        filter.setPageNumber(1);

        assertResponse(ninjaTestBrowser.postJson(getUrl(url), filter), jobApiController.toJobModelHackDto(models.get(1)));
    }

    public void assertResponse(String response, JobModelHackDto dto) {
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.jobModelHackDtos.length()", is(1)));
        assertThat(response, hasJsonPath("$.totalCount", is(2)));

        assertJobModel(response, dto);
    }

    public void assertJobModel(String response, JobModelHackDto dto) {
        assertThat(response, isJson(allOf(
                withJsonPath("$.jobModelHackDtos[0].jobModel.title", is(dto.getJobModel().getTitle())),
                withJsonPath("$.jobModelHackDtos[0].lastExecution", is(dto.getLastExecution())),
                withJsonPath("$.jobModelHackDtos[0].nextExecution", is(dto.getNextExecution())),
                withJsonPath("$.jobModelHackDtos[0].jobModel.id", is(dto.getJobModel().getId())),
                withJsonPath("$.jobModelHackDtos[0].jobModel.name", is(dto.getJobModel().getName()))
        )));

        for (JobLabelModel jobLabelModel : dto.getJobModel().getLabels()) {
            assertThat(response, hasJsonPath("$.jobModelHackDtos[0].jobModel.labels[*].label", hasItem(jobLabelModel.getLabel())));
        }
    }
}
