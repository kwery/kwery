package com.kwery.tests.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.*;
import com.kwery.models.UrlSetting.Scheme;
import com.kwery.models.SqlQueryExecutionModel.Status;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.views.ActionResult;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.*;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.EmailConfiguration.*;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.models.SmtpConfiguration.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class TestUtil {
    public static final int TIMEOUT_SECONDS = 30;

    //Corresponds to the starting id set in *sql file
    public static final int DB_START_ID = 100;

    public static User userWithoutId() {
        User user = new User();
        user.setUsername("purvi");
        user.setPassword("password");
        return user;
    }

    public static User user() {
        User user = new User();
        user.setId(dbId());
        user.setUsername(RandomStringUtils.randomAlphanumeric(1, 256));
        user.setPassword(RandomStringUtils.randomAlphanumeric(1, 256));
        return user;
    }

    public static Datasource datasource(Datasource.Type type) {
        Datasource datasource = datasource();
        datasource.setType(type);
        return datasource;
    }

    public static Datasource datasource() {
        Datasource datasource = new Datasource();
        datasource.setId(DbUtil.dbId());
        datasource.setUrl(RandomStringUtils.randomAlphanumeric(1, 256));
        datasource.setPort(RandomUtils.nextInt(1, 65566));
        datasource.setUsername(RandomStringUtils.randomAlphanumeric(1, 256));
        datasource.setPassword(RandomStringUtils.randomAlphanumeric(1, 256));
        datasource.setLabel(RandomStringUtils.randomAlphanumeric(1, 256));
        datasource.setDatabase(RandomStringUtils.randomAlphanumeric(1, 256));
        datasource.setType(MYSQL);
        return datasource;
    }

    public static JobExecutionModel jobExecutionModel() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(Integer.class, new CustomIdManufacturer());
        JobExecutionModel jobExecutionModel = podamFactory.manufacturePojo(JobExecutionModel.class);
        jobExecutionModel.setSqlQueryExecutionModels(new HashSet<>());
        jobExecutionModel.setJobModel(null);

        return jobExecutionModel;
    }

    public static JobExecutionModel jobExecutionModelWithoutId() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        JobExecutionModel jobExecutionModel = podamFactory.manufacturePojo(JobExecutionModel.class);
        jobExecutionModel.setId(null);
        jobExecutionModel.setSqlQueryExecutionModels(new HashSet<>());
        jobExecutionModel.setJobModel(null);
        return jobExecutionModel;
    }

    public static SqlQueryModel queryRun() {
        SqlQueryModel q = new SqlQueryModel();
        q.setQuery("select * from foo");
        q.setLabel("test query run");
        return q;
    }

    public static SqlQueryModel sleepSqlQuery(Datasource datasource) {
        SqlQueryModel sqlQuery = new SqlQueryModel();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setLabel("test");
        sqlQuery.setQuery("select sleep(86440)");
        return sqlQuery;
    }

    public static SqlQueryDto queryRunDto() {
        SqlQueryDto dto = new SqlQueryDto();
        dto.setQuery("select * from foo");
        dto.setLabel("test");
        return dto;
    }

    public static SqlQueryExecutionModel queryRunExecution() {
        return queryRunExecution(SUCCESS);
    }

    public static SqlQueryExecutionModel queryRunExecution(Status status) {
        SqlQueryExecutionModel e = new SqlQueryExecutionModel();
        e.setExecutionStart(1l);
        e.setExecutionEnd(2l);
        e.setResult("result");
        e.setStatus(status);
        e.setExecutionId("ksjdfjld");
        return e;
    }

    public static SmtpConfiguration smtpConfigurationWithoutId() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        SmtpConfiguration config = podamFactory.manufacturePojo(SmtpConfiguration.class);
        config.setId(null);
        return config;
    }

    public static SmtpConfiguration smtpConfiguration() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        SmtpConfiguration config = podamFactory.manufacturePojo(SmtpConfiguration.class);
        config.setId(1);
        return config;
    }

    public static EmailConfiguration emailConfigurationWithoutId() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        EmailConfiguration emailConfiguration = podamFactory.manufacturePojo(EmailConfiguration.class);
        emailConfiguration.setId(null);
        return emailConfiguration;
    }

    public static EmailConfiguration emailConfiguration() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        EmailConfiguration emailConfiguration = podamFactory.manufacturePojo(EmailConfiguration.class);
        emailConfiguration.setId(1);
        return emailConfiguration;
    }

    public static Datasource datasourceWithoutId() {
        Datasource datasource = datasource();
        datasource.setId(null);
        return datasource;
    }

    public static SqlQueryModel sqlQueryModelWithoutId() {
        SqlQueryModel sqlQueryModel = sqlQueryModel();
        sqlQueryModel.setId(null);
        return sqlQueryModel;
    }

    public static SqlQueryModel sqlQueryModelWithoutId(Datasource datasource) {
        SqlQueryModel sqlQueryModel = sqlQueryModelWithoutId();
        sqlQueryModel.setDatasource(datasource);
        return sqlQueryModel;
    }

    public static SqlQueryModel sqlQueryModel() {
        SqlQueryModel sqlQueryModel = new SqlQueryModel();
        sqlQueryModel.setId(DbUtil.dbId());
        sqlQueryModel.setQuery(RandomStringUtils.randomAlphanumeric(SqlQueryModel.QUERY_MIN_LENGTH, SqlQueryModel.QUERY_MAX_LENGTH + 1));
        sqlQueryModel.setLabel(RandomStringUtils.randomAlphanumeric(1, 256));
        sqlQueryModel.setTitle(RandomStringUtils.randomAlphanumeric(1, 1025));
        return sqlQueryModel;
    }

    public static SqlQueryModel sqlQueryModel(Datasource datasource) {
        SqlQueryModel sqlQueryModel = sqlQueryModel();
        sqlQueryModel.setDatasource(datasource);
        return sqlQueryModel;
    }

    public static JobModel jobModelWithoutDependents() {
        return jobModel();
    }

    public static JobModel jobModelWithoutIdWithoutDependents() {
        JobModel jobModel = jobModel();
        jobModel.setId(null);
        return jobModel;
    }

    private static JobModel jobModel() {
        JobModel jobModel = new JobModel();
        jobModel.setId(DbUtil.dbId());
        jobModel.setCronExpression("* * * * *");
        jobModel.setName(RandomStringUtils.randomAlphanumeric(1, 256));
        jobModel.setTitle(RandomStringUtils.randomAlphanumeric(1, 1024));
        jobModel.setChildJobs(new HashSet<>());
        jobModel.setEmails(new HashSet<>());
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setParentJob(null);
        jobModel.setLabels(new HashSet<>());
        return jobModel;
    }

    public static SqlQueryExecutionModel sqlQueryExecutionModelWithoutId() {
        SqlQueryExecutionModel model = new PodamFactoryImpl().manufacturePojo(SqlQueryExecutionModel.class);
        model.setId(null);
        model.setSqlQuery(null);
        model.setJobExecutionModel(null);
        return model;
    }

    public static SqlQueryExecutionModel sqlQueryExecutionModel() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(Integer.class, new CustomIdManufacturer());
        SqlQueryExecutionModel model = podamFactory.manufacturePojo(SqlQueryExecutionModel.class);
        model.setSqlQuery(null);
        model.setJobExecutionModel(null);
        return model;
    }

    public static JobDto jobDtoWithoutId() {
        JobDto jobDto = jobDto();
        jobDto.setId(0);
        return jobDto;
    }

    public static JobDto jobDto() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(Integer.class, new CustomIdManufacturer());
        JobDto jobDto = podamFactory.manufacturePojo(JobDto.class);
        jobDto.setEmails(new HashSet<>());
        jobDto.setSqlQueries(new ArrayList<>());
        jobDto.setLabelIds(new HashSet<>());
        return jobDto;
    }

    public static SqlQueryDto sqlQueryDtoWithoutId() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        SqlQueryDto sqlQueryDto = podamFactory.manufacturePojo(SqlQueryDto.class);
        sqlQueryDto.setId(0);
        return sqlQueryDto;
    }

    public static SqlQueryDto sqlQueryDto() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(Integer.class, new CustomIdManufacturer());
        return podamFactory.manufacturePojo(SqlQueryDto.class);
    }

    public static EmailConfiguration emailConfigurationDbSetUp() {
        EmailConfiguration emailConfiguration = emailConfiguration();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(TABLE_EMAIL_CONFIGURATION)
                                .row()
                                .column(EmailConfiguration.COLUMN_ID, emailConfiguration.getId())
                                .column(COLUMN_FROM_EMAIL, emailConfiguration.getFrom())
                                .column(COLUMN_BCC, emailConfiguration.getBcc())
                                .column(COLUMN_REPLY_TO, emailConfiguration.getReplyTo())
                                .end()
                                .build()
                )
        ).launch();

        return emailConfiguration;
    }

    public static SmtpConfiguration smtpConfigurationDbSetUp() {
        SmtpConfiguration smtpConfiguration = smtpConfiguration();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(TABLE_SMTP_CONFIGURATION)
                                .row()
                                .column(SmtpConfiguration.COLUMN_ID, smtpConfiguration.getId())
                                .column(COLUMN_HOST, smtpConfiguration.getHost())
                                .column(COLUMN_PORT, smtpConfiguration.getPort())
                                .column(COLUMN_SSL, smtpConfiguration.isSsl())
                                .column(COLUMN_USERNAME, smtpConfiguration.getUsername())
                                .column(COLUMN_PASSWORD, smtpConfiguration.getPassword())
                                .end()
                                .build()
                )
        ).launch();

        return smtpConfiguration;
    }

    public static String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
        }
        return "";
    }

    public static JobModel toJobModel(JobDto jobDto, Datasource datasource) {
        JobModel jobModel = new JobModel();
        jobModel.setName(jobDto.getName());
        jobModel.setTitle(jobDto.getTitle());
        jobModel.setCronExpression(jobDto.getCronExpression());
        jobModel.setEmails(jobDto.getEmails());
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setChildJobs(new HashSet<>());

        for (SqlQueryDto sqlQueryDto : jobDto.getSqlQueries()) {
            SqlQueryModel sqlQueryModel = new SqlQueryModel();
            sqlQueryModel.setLabel(sqlQueryDto.getLabel());
            sqlQueryModel.setTitle(sqlQueryDto.getTitle());
            sqlQueryModel.setQuery(sqlQueryDto.getQuery());
            sqlQueryModel.setDatasource(datasource);
            jobModel.getSqlQueries().add(sqlQueryModel);
        }

        return jobModel;
    }

    public static <T> List<T> toList(Collection<T> col) {
        return new ArrayList<T>(col);
    }

    public static void assertJobModel(JobModel jobModel, JobModel parentJobModel, JobDto jobDto, Datasource datasource) {
        JobModel expectedJobModel = toJobModel(jobDto, datasource);

        expectedJobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));

        if (parentJobModel != null) {
            expectedJobModel.setParentJob(parentJobModel);
        }

        assertThat(jobModel, theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("sqlQueries"));

        List<SqlQueryModel> expectedSqlQueryModels = toList(expectedJobModel.getSqlQueries());
        sort(expectedSqlQueryModels, comparing(SqlQueryModel::getLabel));

        List<SqlQueryModel> sqlQueryModelsFromDb = toList(jobModel.getSqlQueries());
        sort(sqlQueryModelsFromDb, comparing(SqlQueryModel::getLabel));

        List<Pair<SqlQueryModel>> pairs = new ArrayList<>();

        for (SqlQueryModel sqlQueryModel : sqlQueryModelsFromDb) {
            pairs.add(new Pair<>(sqlQueryModel, null));
        }

        for (int i = 0; i < pairs.size(); i++) {
            pairs.get(i).setSecond(expectedSqlQueryModels.get(i));
        }

        for (Pair<SqlQueryModel> pair : pairs) {
            assertThat(pair.getFirst(), theSameBeanAs(pair.getSecond()).excludeProperty("id"));
        }
    }

    public static JobLabelModel jobLabelModel() {
        JobLabelModel m = new JobLabelModel();
        m.setId(dbId());
        m.setLabel(RandomStringUtils.randomAlphanumeric(JobLabelModel.LABEL_MIN_LENGTH, JobLabelModel.LABEL_MAX_LENGTH + 1));
        m.setChildLabels(new HashSet<>());
        return m;
    }

    public static UrlSetting domainSettingWithoutId() {
        UrlSetting d = new UrlSetting();
        d.setPort(RandomUtils.nextInt(UrlSetting.PORT_MIN, UrlSetting.PORT_MAX + 1));
        d.setDomain(RandomStringUtils.randomAlphanumeric(UrlSetting.DOMAIN_MIN, UrlSetting.DOMAIN_MAX + 1));
        d.setScheme(Scheme.values()[RandomUtils.nextInt(0, 2)]);
        return d;
    }

    public static UrlSetting domainSetting() {
        UrlSetting d = domainSettingWithoutId();
        d.setId(dbId());
        return d;
    }

    public static void assertJsonActionResult(String response, ActionResult expected) {
        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(expected.getStatus().name())));
        if (expected.getMessages() != null) {
            assertThat(response, hasJsonPath("$.messages.length()", is(expected.getMessages().size())));
            for (String message : expected.getMessages()) {
                assertThat(response, hasJsonPath("$.messages", hasItem(message)));
            }
        }
    }
}
