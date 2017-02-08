package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.kwery.controllers.apis.JobApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobLabelDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.*;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobApiControllerJobDtoToJobModelTest extends RepoDashDaoTestBase {
    protected JobApiController jobApiController;
    protected DatasourceDao datasourceDao;

    protected int datasourceId = 1;

    protected final Set<String> emails = ImmutableSet.of("foo@bar.com", "goo@moo.com");
    private JobLabelDao jobLabelDao;
    private Set<Integer> jobLabelIds;
    private ImmutableSet<JobLabelModel> jobLabelModels;
    protected final Set<String> failureAlertEmails = ImmutableSet.of("goo@cho.com", "loo@poo.com");

    @Before
    public void setUpJobApiControllerJobDtoToJobModelTest() {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasourceId, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "username")
                                .build()
                )
        ).launch();

        JobLabelModel jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        JobLabelModel jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        jobLabelModels = ImmutableSet.of(jobLabelModel0, jobLabelModel1);
        jobLabelIds = jobLabelModels.stream().map(JobLabelModel::getId).collect(Collectors.toSet());

        datasourceDao = getInstance(DatasourceDao.class);
        jobLabelDao = getInstance(JobLabelDao.class);

        jobApiController = new JobApiController(datasourceDao, getInstance(JobDao.class), null, null, null,
                null, jobLabelDao, null, null);
    }

    @Test
    public void testWithoutId() {
        JobDto jobDto = jobDtoWithoutId();
        jobDto.setEmails(emails);
        jobDto.setJobFailureAlertEmails(failureAlertEmails);
        jobDto.setLabelIds(jobLabelIds);

        jobDto.setSqlQueries(new ArrayList<>(2));

        JobRuleModel jobRuleModel = jobRuleModelWithoutId();
        jobDto.setJobRuleModel(jobRuleModel);

        JobModel jobModel = new JobModel();
        jobModel.setName(jobDto.getName());
        jobModel.setTitle(jobDto.getTitle());
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setId(null);
        jobModel.setEmails(emails);
        jobModel.setFailureAlertEmails(failureAlertEmails);
        jobModel.setLabels(jobLabelModels);

        jobModel.setJobRuleModel(jobRuleModel);

        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
            sqlQueryDto.setDatasourceId(datasourceId);
            jobDto.getSqlQueries().add(sqlQueryDto);

            SqlQueryModel model = new SqlQueryModel();
            model.setQuery(sqlQueryDto.getQuery());
            model.setLabel(sqlQueryDto.getLabel());
            model.setDatasource(datasourceDao.getById(datasourceId));
            model.setTitle(sqlQueryDto.getTitle());

            jobModel.getSqlQueries().add(model);
        }

        assertThat(jobModel, theSameBeanAs(jobApiController.jobDtoToJobModel(jobDto)));
    }

    @Test
    public void testWithId() {
        JobDto jobDto = TestUtil.jobDto();
        jobDto.setEmails(emails);
        jobDto.setJobFailureAlertEmails(failureAlertEmails);
        jobDto.setSqlQueries(new ArrayList<>(2));
        jobDto.setLabelIds(jobLabelIds);

        JobRuleModel jobRuleModel = jobRuleModel();
        jobDto.setJobRuleModel(jobRuleModel);

        JobModel jobModel = new JobModel();
        jobModel.setName(jobDto.getName());
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setTitle(jobDto.getTitle());
        jobModel.setId(null);
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setId(jobDto.getId());
        jobModel.setEmails(emails);
        jobModel.setFailureAlertEmails(failureAlertEmails);
        jobModel.setLabels(jobLabelModels);
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));
        jobModel.setJobRuleModel(jobRuleModel);

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDto();
            sqlQueryDto.setDatasourceId(datasourceId);
            jobDto.getSqlQueries().add(sqlQueryDto);

            SqlQueryModel model = new SqlQueryModel();
            model.setQuery(sqlQueryDto.getQuery());
            model.setLabel(sqlQueryDto.getLabel());
            model.setDatasource(datasourceDao.getById(datasourceId));
            model.setId(sqlQueryDto.getId());
            model.setTitle(sqlQueryDto.getTitle());

            jobModel.getSqlQueries().add(model);
        }

        assertThat(jobModel, theSameBeanAs(jobApiController.jobDtoToJobModel(jobDto)));
    }

    @Test
    public void testNullLabelIds() {
        JobDto jobDto = jobDtoWithoutId();
        jobDto.setLabelIds(null);

        JobModel jobModel = new JobModel();
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setTitle(jobDto.getTitle());
        jobModel.setName(jobDto.getName());
        jobModel.setId(null);
        jobModel.setLabels(new HashSet<>());
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(new HashSet<>());
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));

        assertThat(jobModel, theSameBeanAs(jobApiController.jobDtoToJobModel(jobDto)));
    }

    @Test
    public void testEmptyLabelIds() {
        JobDto jobDto = jobDtoWithoutId();
        jobDto.setLabelIds(new HashSet<>());

        JobModel jobModel = new JobModel();
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setTitle(jobDto.getTitle());
        jobModel.setName(jobDto.getName());
        jobModel.setId(null);
        jobModel.setLabels(new HashSet<>());
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(new HashSet<>());
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));

        assertThat(jobModel, theSameBeanAs(jobApiController.jobDtoToJobModel(jobDto)));
    }

    @Test
    public void testLabelIdWithZeroesAndNulls() {
        JobDto jobDto = jobDtoWithoutId();
        jobDto.setLabelIds(Sets.newHashSet(0, 0, null, null));

        JobModel jobModel = new JobModel();
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setTitle(jobDto.getTitle());
        jobModel.setName(jobDto.getName());
        jobModel.setId(null);
        jobModel.setLabels(new HashSet<>());
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setEmails(new HashSet<>());
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));

        assertThat(jobModel, theSameBeanAs(jobApiController.jobDtoToJobModel(jobDto)));
    }
}
