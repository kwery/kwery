package com.kwery.tests.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.JobModelHackDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.*;
import com.kwery.models.SqlQueryExecutionModel.Status;
import com.kwery.models.UrlConfiguration.Scheme;
import com.kwery.utils.CsvWriterFactoryImpl;
import com.kwery.views.ActionResult;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import net.sf.ehcache.management.sampled.SampledMBeanRegistrationProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.activation.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.dbId;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class TestUtil {
    public static final int TIMEOUT_SECONDS = 30;

    private static Map<Class<?>, Set<Integer>> createdIds = new HashMap<>();

    //Corresponds to the starting id set in *sql file
    public static final int DB_START_ID = 100;

    public static User userWithoutId() {
        User user = user();
        user.setId(null);
        return user;
    }

    public static User user() {
        User user = new User();
        user.setId(getId(User.class));
        user.setPassword(RandomStringUtils.randomAlphanumeric(1, 256));
        user.setFirstName(RandomStringUtils.randomAlphanumeric(User.FIRST_NAME_MIN, User.FIRST_NAME_MAX + 1));
        user.setMiddleName(RandomStringUtils.randomAlphanumeric(User.MIDDLE_NAME_MIN, User.MIDDLE_NAME_MAX + 1));
        user.setLastName(RandomStringUtils.randomAlphanumeric(User.LAST_NAME_MIN, User.LAST_NAME_MAX + 1));
        user.setEmail(RandomStringUtils.randomAlphanumeric(User.EMAIL_MIN, User.EMAIL_MAX - 9) + "@gmail.com");
        user.setCreated(System.currentTimeMillis());
        user.setUpdated(System.currentTimeMillis());
        return user;
    }

    public static Datasource datasource(Datasource.Type type) {
        Datasource datasource = datasource();
        datasource.setType(type);
        return datasource;
    }

    public static Datasource datasource() {
        Datasource datasource = new Datasource();
        datasource.setId(getId(DataSource.class));
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
        JobExecutionModel jobExecutionModel = podamFactory.manufacturePojo(JobExecutionModel.class);
        jobExecutionModel.setSqlQueryExecutionModels(new HashSet<>());
        jobExecutionModel.setJobModel(null);
        jobExecutionModel.setId(getId(JobExecutionModel.class));

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
        e.setExecutionError("result");
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
        config.setId(getId(SmtpConfiguration.class));
        return config;
    }

    public static EmailConfiguration emailConfigurationWithoutId() {
        EmailConfiguration configuration = emailConfiguration();
        configuration.setId(null);
        return configuration;
    }

    public static EmailConfiguration emailConfiguration() {
        EmailConfiguration emailConfiguration = new EmailConfiguration();
        emailConfiguration.setBcc(RandomStringUtils.randomAlphanumeric(1, 100) + "@getkwery.com");
        emailConfiguration.setReplyTo(RandomStringUtils.randomAlphanumeric(1, 100) + "@getkwery.com");
        emailConfiguration.setFrom(RandomStringUtils.randomAlphanumeric(1, 100) + "@getkwery.com");
        emailConfiguration.setId(getId(EmailConfiguration.class));
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
        sqlQueryModel.setId(getId(SqlQueryModel.class));
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
        jobModel.setId(getId(JobModel.class));
        jobModel.setCronExpression("* * * * *");
        jobModel.setName(RandomStringUtils.randomAlphanumeric(1, 256));
        jobModel.setTitle(RandomStringUtils.randomAlphanumeric(1, 1024));
        jobModel.setChildJobs(new HashSet<>());
        jobModel.setEmails(new HashSet<>());
        jobModel.setSqlQueries(new LinkedList<>());
        jobModel.setParentJob(null);
        jobModel.setLabels(new HashSet<>());
        jobModel.setJobRuleModel(null);
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
        SqlQueryExecutionModel model = podamFactory.manufacturePojo(SqlQueryExecutionModel.class);
        model.setId(getId(SqlQueryExecutionModel.class));
        model.setSqlQuery(null);
        model.setJobExecutionModel(null);
        return model;
    }

    public static JobDto jobDtoWithoutId() {
        JobDto jobDto = jobDto();
        jobDto.setJobRuleModel(null);
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
        jobDto.setJobFailureAlertEmails(new HashSet<>());
        jobDto.setJobRuleModel(null);
        return jobDto;
    }

    public static SqlQueryDto sqlQueryDtoWithoutId() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        SqlQueryDto sqlQueryDto = podamFactory.manufacturePojo(SqlQueryDto.class);
        sqlQueryDto.setId(0);
        sqlQueryDto.setSqlQueryEmailSetting(null);
        return sqlQueryDto;
    }

    public static SqlQueryDto sqlQueryDto() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(Integer.class, new CustomIdManufacturer());
        SqlQueryDto sqlQueryDto = podamFactory.manufacturePojo(SqlQueryDto.class);
        sqlQueryDto.setSqlQueryEmailSetting(null);
        return sqlQueryDto;
    }

    public static SqlQueryEmailSettingModel sqlQueryEmailSettingModel() {
        SqlQueryEmailSettingModel sqlQueryEmailSettingModel = new SqlQueryEmailSettingModel();
        sqlQueryEmailSettingModel.setId(getId(SqlQueryExecutionModel.class));
        sqlQueryEmailSettingModel.setIncludeInEmailAttachment(new Boolean[]{true, false}[RandomUtils.nextInt(0, 2)]);
        sqlQueryEmailSettingModel.setIncludeInEmailBody(new Boolean[]{true, false}[RandomUtils.nextInt(0, 2)]);
        return sqlQueryEmailSettingModel;
    }

    public static SqlQueryEmailSettingModel sqlQueryEmailSettingModelWithoutId() {
        SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryEmailSettingModel();
        sqlQueryEmailSettingModel.setId(null);
        return sqlQueryEmailSettingModel;
    }

    public static JobRuleModel jobRuleModel() {
        JobRuleModel jobRuleModel = new JobRuleModel();
        jobRuleModel.setId(getId(JobRuleModel.class));
        jobRuleModel.setSequentialSqlQueryExecution(new Boolean[]{true, false}[RandomUtils.nextInt(0, 2)]);

        if (!jobRuleModel.isSequentialSqlQueryExecution()) {
            jobRuleModel.setStopExecutionOnSqlQueryFailure(false);
        } else {
            jobRuleModel.setStopExecutionOnSqlQueryFailure(new Boolean[]{true, false}[RandomUtils.nextInt(0, 2)]);
        }

        return jobRuleModel;
    }

    public static JobRuleModel jobRuleModelWithoutId() {
        JobRuleModel jobRuleModel = jobRuleModel();
        jobRuleModel.setId(null);
        return jobRuleModel;
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
        jobModel.setJobRuleModel(jobDto.getJobRuleModel());

        for (SqlQueryDto sqlQueryDto : jobDto.getSqlQueries()) {
            SqlQueryModel sqlQueryModel = new SqlQueryModel();
            sqlQueryModel.setLabel(sqlQueryDto.getLabel());
            sqlQueryModel.setTitle(sqlQueryDto.getTitle());
            sqlQueryModel.setQuery(sqlQueryDto.getQuery());
            sqlQueryModel.setDatasource(datasource);

            if (sqlQueryDto.getSqlQueryEmailSetting() == null) {
                SqlQueryEmailSettingModel model = new SqlQueryEmailSettingModel();
                model.setIncludeInEmailAttachment(true);
                model.setIncludeInEmailBody(true);
                sqlQueryModel.setSqlQueryEmailSettingModel(model);
            } else {
                sqlQueryModel.setSqlQueryEmailSettingModel(sqlQueryDto.getSqlQueryEmailSetting());
            }

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

        assertThat(jobModel, theSameBeanAs(expectedJobModel).excludeProperty("id").excludeProperty("sqlQueries").excludeProperty("jobRuleModel.id"));

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
            assertThat(pair.getFirst(), theSameBeanAs(pair.getSecond()).excludeProperty("id").excludeProperty("sqlQueryEmailSettingModel.id"));
        }
    }

    public static JobLabelModel jobLabelModel() {
        JobLabelModel m = new JobLabelModel();
        m.setId(getId(JobLabelModel.class));
        m.setLabel(RandomStringUtils.randomAlphanumeric(JobLabelModel.LABEL_MIN_LENGTH, JobLabelModel.LABEL_MAX_LENGTH + 1));
        m.setChildLabels(new HashSet<>());
        return m;
    }

    public static JobLabelModel jobLabelModelWithoutId() {
        JobLabelModel jobLabelModel = jobLabelModel();
        jobLabelModel.setId(null);
        return jobLabelModel;
    }

    public static UrlConfiguration domainSettingWithoutId() {
        UrlConfiguration d = new UrlConfiguration();
        d.setPort(RandomUtils.nextInt(UrlConfiguration.PORT_MIN, UrlConfiguration.PORT_MAX + 1));
        d.setDomain(RandomStringUtils.randomAlphanumeric(UrlConfiguration.DOMAIN_MIN, UrlConfiguration.DOMAIN_MAX + 1));
        d.setScheme(Scheme.values()[RandomUtils.nextInt(0, 2)]);
        return d;
    }

    public static UrlConfiguration domainSetting() {
        UrlConfiguration d = domainSettingWithoutId();
        d.setId(getId(UrlConfiguration.class));
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

    public static void writeCsv(List<String[]> data, File file) throws Exception {
        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter);
             CSVWriter csvWriter = new CsvWriterFactoryImpl().create(printWriter)
        ) {
            csvWriter.writeAll(data);
        }
    }

    public static void writeCsvOfLines(long lineCount, File file) throws Exception {
        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter);
             CSVWriter csvWriter = new CsvWriterFactoryImpl().create(printWriter)
        ) {

            for (long i = 0; i < lineCount; ++i) {
                csvWriter.writeNext(new String[]{RandomStringUtils.randomAlphabetic(1, 10)});
            }
        }
    }

    public static void writeCsvOfSize(long sizeInBytes, File file) throws Exception {
        do {
            try (FileWriter fileWriter = new FileWriter(file, true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                 PrintWriter printWriter = new PrintWriter(bufferedWriter);
                 CSVWriter csvWriter = new CsvWriterFactoryImpl().create(printWriter)
            ) {
                for (long i = 0; i < 10000; ++i) { //Randomly chosen number
                    csvWriter.writeNext(new String[]{
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10),
                            RandomStringUtils.randomAlphabetic(1, 10)
                    });
                }

            }
        } while (file.length() < sizeInBytes);
    }

    //http://stackoverflow.com/questions/245251/create-file-with-given-size-in-java
    public static void writeFileOfSize(long sizeInBytes, File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file.getPath(), "rw");
        f.setLength(sizeInBytes);
    }

    public static void main(String[] args) throws Exception {
        File file = new File("/tmp/foo");
        file.createNewFile();
        writeFileOfSize(15100000, file);
    }

    public static String toString(DataSource dataSource) throws IOException {
        try (InputStream inputStream = dataSource.getInputStream()) {
            return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        }
    }

    public static String toString(File file) throws IOException {
        Path path = Paths.get(file.toURI());
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    public static void assertJsonJobModel(String response, int index, JobModelHackDto dto) {
        assertThat(response, isJson(allOf(
                withJsonPath(String.format("$.[%d].jobModel.title", index), is(dto.getJobModel().getTitle())),
                withJsonPath(String.format("$.[%d].lastExecution", index), is(dto.getLastExecution())),
                withJsonPath(String.format("$.[%d].nextExecution", index) , is(dto.getNextExecution())),
                withJsonPath(String.format("$.[%d].jobModel.id", index), is(dto.getJobModel().getId())),
                withJsonPath(String.format("$.[%d].jobModel.name", index), is(dto.getJobModel().getName()))
        )));

        for (JobLabelModel jobLabelModel : dto.getJobModel().getLabels()) {
            assertThat(response, hasJsonPath(String.format("$.[%d].jobModel.labels[*].label", index), hasItem(jobLabelModel.getLabel())));
        }
    }

    public static void assertActionResultStatus(String response, ActionResult.Status status) {
        assertThat(response, isJson(allOf(
                withJsonPath("$.status", is(status.name()))
        )));
    }

    public static void assertActionResult(String response, ActionResult actionResult) {
        assertThat(response, isJson(allOf(
                withJsonPath("$.status", is(actionResult.getStatus().name()))
        )));

        assertThat(response, hasJsonPath("$.messages.length()", is(actionResult.getMessages().size())));

        for (String message : actionResult.getMessages()) {
            assertThat(response, hasJsonPath("$.messages[*]", hasItem(message)));
        }

    }

    public static void assertUser(String response, User user) {
        assertThat(response, isJson(allOf(
                withJsonPath("$.id", is(user.getId())),
                withJsonPath("$.firstName", is(user.getFirstName())),
                withJsonPath("$.middleName", is(user.getMiddleName())),
                withJsonPath("$.lastName" , is(user.getLastName())),
                withJsonPath("$.email", is(user.getEmail())),
                withJsonPath("$.password", is(user.getPassword()))
        )));
    }

    private static Integer getId(Class<?> claz) {
        int id = dbId();
        if (!createdIds.containsKey(claz)) {
            createdIds.put(claz, Sets.newHashSet(id));
        } else {
            int tries = 0;
            do {
                if (createdIds.get(claz).contains(id)) {
                    id = dbId();
                } else {
                    createdIds.get(claz).add(id);
                    break;
                }
                tries = tries + 1;
            } while (tries < TestUtil.DB_START_ID);

            if (tries >= TestUtil.DB_START_ID) {
                throw new RuntimeException("Could not generate a unique id");
            }
        }

        return id;
    }

    public static void clearCreatedIds() {
        createdIds.clear();
    }
}
