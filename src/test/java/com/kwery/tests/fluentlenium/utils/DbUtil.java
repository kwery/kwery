package com.kwery.tests.fluentlenium.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.kwery.models.*;
import com.kwery.tests.util.TestUtil;
import com.mchange.v2.c3p0.C3P0Registry;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.apache.commons.lang3.RandomUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobModel.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static java.util.Arrays.asList;
import static org.dbunit.Assertion.assertEquals;

public class DbUtil {
    public static javax.sql.DataSource getDatasource() {
        Set set = C3P0Registry.getPooledDataSources();
        return (javax.sql.DataSource) set.iterator().next();
    }

    public static void assertDbState(String tableName, String dataFile) throws SQLException, DatabaseUnitException, IOException {
        IDatabaseConnection connection = new DatabaseConnection(DbUtil.getDatasource().getConnection());
        try {
            IDataSet databaseDataSet = connection.createDataSet();
            ITable actualTable = databaseDataSet.getTable(tableName);

            IDataSet expectedDataSet = new FlatXmlDataFileLoader().loadDataSet(Resources.getResource(dataFile));
            ITable expectedTable = expectedDataSet.getTable(tableName);

            assertEquals(expectedTable, actualTable);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static void assertDbState(String tableName, IDataSet expectedDataSet) throws SQLException, DatabaseUnitException, IOException {
        assertDbState(tableName, expectedDataSet, null);
    }

    public static void assertDbState(String tableName, IDataSet expectedDataSet, String... columnsToIgnore) throws SQLException, DatabaseUnitException, IOException {
        IDatabaseConnection connection = new DatabaseConnection(DbUtil.getDatasource().getConnection());
        try {
            IDataSet databaseDataSet = connection.createDataSet();
            ITable actualTable = databaseDataSet.getTable(tableName);
            ITable expectedTable = expectedDataSet.getTable(tableName);

            if (columnsToIgnore != null) {
                actualTable = DefaultColumnFilter.excludedColumnsTable(actualTable, columnsToIgnore);
                expectedTable = DefaultColumnFilter.excludedColumnsTable(expectedTable, columnsToIgnore);
            }

            boolean sortById = false;

            if (columnsToIgnore == null || !asList(columnsToIgnore).contains("id")) {
                for (Column column : expectedDataSet.getTableMetaData(tableName).getColumns()) {
                    if ("id".equals(column.getColumnName())) {
                        sortById = true;
                        break;
                    }
                }
            }

            if (sortById) {
                SortedTable actualTable1 = new SortedTable(actualTable, new String[]{"id"});
                actualTable1.setUseComparable(true);

                SortedTable expectedTable1 = new SortedTable(new TableWrapper(expectedTable, actualTable1.getTableMetaData()), new String[]{"id"});
                expectedTable1.setUseComparable(true);

                assertEquals(expectedTable1, actualTable1);
            } else {
                SortedTable expectedTable1 = new SortedTable(expectedTable);
                expectedTable1.setUseComparable(true);

                SortedTable actualTable1 = new SortedTable(new TableWrapper(actualTable, actualTable.getTableMetaData()));
                actualTable1.setUseComparable(true);

                assertEquals(expectedTable1, actualTable1);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static IDataSet jobTable(JobModel m) throws DataSetException {
        return jobTable(ImmutableList.of(m));
    }

    public static IDataSet jobEmailTable(JobModel... ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JobModel.JOB_EMAIL_TABLE);

        if (ms != null) {
            for (JobModel m : ms) {
                for (String s : m.getEmails()) {
                    builder.newRow(JobModel.JOB_EMAIL_TABLE)
                            .with(JobModel.JOB_EMAIL_TABLE_JOB_ID_FK_COLUMN, m.getId())
                            .with(JobModel.JOB_EMAIL_TABLE_EMAIL_COLUMN, s)
                            .add();
                }
            }
        }

        return builder.build();
    }

    public static IDataSet jobFailureAlertEmailTable(JobModel... ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JobModel.JOB_FAILURE_ALERT_EMAIL_TABLE);

        if (ms != null) {
            for (JobModel m : ms) {
                for (String s : m.getFailureAlertEmails()) {
                    builder.newRow(JobModel.JOB_FAILURE_ALERT_EMAIL_TABLE)
                            .with(JobModel.JOB_FAILURE_ALERT_EMAIL_TABLE_JOB_ID_FK_COLUMN, m.getId())
                            .with(JobModel.JOB_FAILURE_ALERT_EMAIL_TABLE_EMAIL_COLUMN, s)
                            .add();
                }
            }
        }

        return builder.build();
    }

    public static IDataSet jobTable(List<JobModel> ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JOB_TABLE);

        for (JobModel m : ms) {
            builder.newRow(JOB_TABLE)
                    .with(JobModel.NAME_COLUMN, m.getName())
                    .with(JobModel.CRON_EXPRESSION_COLUMN, m.getCronExpression())
                    .with(JobModel.TITLE_COLUMN, m.getTitle())
                    .with(JobModel.ID_COLUMN, m.getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet jobDependentTable(JobModel jobModel) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JobModel.JOB_CHILDREN_TABLE);

        if (jobModel != null) {
            for (JobModel m : jobModel.getChildJobs()) {
                builder.newRow(JobModel.JOB_CHILDREN_TABLE)
                        .with(JobModel.JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN, jobModel.getId())
                        .with(JobModel.JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN, m.getId())
                        .add();
            }
        }

        return builder.build();
    }

    public static IDataSet sqlQueryTable(SqlQueryModel m) throws DataSetException {
        return sqlQueryTable(ImmutableList.of(m));
    }

    public static IDataSet sqlQueryTable(Collection<SqlQueryModel> ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(SqlQueryModel.SQL_QUERY_TABLE);

        for (SqlQueryModel m : ms) {
            builder.newRow(SqlQueryModel.SQL_QUERY_TABLE)
                    .with(SqlQueryModel.ID_COLUMN, m.getId())
                    .with(SqlQueryModel.LABEL_COLUMN, m.getLabel())
                    .with(SqlQueryModel.QUERY_COLUMN, m.getQuery())
                    .with(SqlQueryModel.TITLE_COLUMN, m.getTitle())
                    .with(SqlQueryModel.DATASOURCE_ID_FK_COLUMN, m.getDatasource().getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet jobSqlQueryTable(JobModel... jobModels) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JOB_SQL_QUERY_TABLE);

        if (jobModels != null) {
            for (JobModel jobModel : jobModels) {
                for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
                    builder.newRow(JOB_SQL_QUERY_TABLE)
                            .with(JobModel.JOB_ID_FK_COLUMN, jobModel.getId())
                            .with(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                            .add();
                }
            }
        }

        return builder.build();
    }

    public static IDataSet jobExecutionTable(JobExecutionModel jobExecutionModel) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JobExecutionModel.TABLE);

        if (jobExecutionModel != null) {
            builder.newRow(JobExecutionModel.TABLE)
                    .with(JobExecutionModel.COLUMN_ID, jobExecutionModel.getId())
                    .with(JobExecutionModel.COLUMN_EXECUTION_ID, jobExecutionModel.getExecutionId())
                    .with(JobExecutionModel.COLUMN_EXECUTION_START, jobExecutionModel.getExecutionStart())
                    .with(JobExecutionModel.COLUMN_EXECUTION_END, jobExecutionModel.getExecutionEnd())
                    .with(JobExecutionModel.COLUMN_STATUS, jobExecutionModel.getStatus())
                    .with(JobExecutionModel.JOB_ID_FK_COLUMN, jobExecutionModel.getJobModel().getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet sqlQueryExecutionTable(SqlQueryExecutionModel model) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(SqlQueryExecutionModel.TABLE);

        if (model != null) {
            builder.newRow(SqlQueryExecutionModel.TABLE)
                    .with(SqlQueryExecutionModel.COLUMN_ID, model.getId())
                    .with(SqlQueryExecutionModel.COLUMN_EXECUTION_ID, model.getExecutionId())
                    .with(SqlQueryExecutionModel.COLUMN_EXECUTION_START, model.getExecutionStart())
                    .with(SqlQueryExecutionModel.COLUMN_EXECUTION_END, model.getExecutionEnd())
                    .with(SqlQueryExecutionModel.COLUMN_RESULT, model.getResult())
                    .with(SqlQueryExecutionModel.COLUMN_STATUS, model.getStatus())
                    .with(SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK, model.getSqlQuery().getId())
                    .with(SqlQueryExecutionModel.COLUMN_JOB_EXECUTION_ID_FK, model.getJobExecutionModel().getId())
                    .add();
        }


        return builder.build();
    }

    public static IDataSet datasourceTable(Datasource datasource) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(Datasource.TABLE);

        if (datasource != null) {
            builder.newRow(Datasource.TABLE)
                        .with(COLUMN_ID, datasource.getId())
                        .with(COLUMN_LABEL, datasource.getLabel())
                        .with(COLUMN_PASSWORD, datasource.getPassword())
                        .with(COLUMN_PORT, datasource.getPort())
                        .with(COLUMN_TYPE, datasource.getType())
                        .with(COLUMN_URL, datasource.getUrl())
                        .with(COLUMN_USERNAME, datasource.getUsername())
                        .with(COLUMN_DATABASE, datasource.getDatabase())
                    .add();

        }

        return builder.build();
    }

    public static IDataSet jobLabelTable(JobLabelModel... ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JobLabelModel.JOB_LABEL_TABLE);

        if (ms != null) {
            for (JobLabelModel m : ms) {
                builder.newRow(JobLabelModel.JOB_LABEL_TABLE)
                        .with(JobLabelModel.ID_COLUMN, m.getId())
                        .with(JobLabelModel.LABEL_COLUMN, m.getLabel())
                        .with(JobLabelModel.PARENT_LABEL_ID_FK_COLUMN, m.getParentLabel() == null ? null : m.getParentLabel().getId())
                        .add();
            }
        }

        return builder.build();
    }

    public static IDataSet jobJobLabelTable(JobModel... ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JobModel.JOB_JOB_LABEL_TABLE);

        if (ms != null) {
            for (JobModel m : ms) {
                for (JobLabelModel jobLabelModel : m.getLabels()) {
                    builder.newRow(JOB_JOB_LABEL_TABLE)
                            .with(JOB_JOB_LABEL_TABLE_FK_JOB_ID_COLUMN, m.getId())
                            .with(JOB_JOB_LABEL_TABLE_FK_JOB_LABEL_ID_COLUMN, jobLabelModel.getId())
                            .add();
                }
            }
        }

        return builder.build();
    }

    public static IDataSet jobRuleTable(JobModel... ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(JobModel.JOB_RULE_TABLE);

        if (ms != null) {
            for (JobModel m : ms) {
                for (Map.Entry<Rules, String> rule : m.getRules().entrySet()) {
                    builder.newRow(JOB_RULE_TABLE)
                            .with(JobModel.JOB_RULE_TABLE_NAME_COLUMN, rule.getKey())
                            .with(JobModel.JOB_RULE_TABLE_VALUE_COLUMN, rule.getValue())
                            .with(JobModel.JOB_RULE_JOB_ID_FK_COLUMN, m.getId())
                            .add();
                }
            }
        }

        return builder.build();
    }

    public static IDataSet smtpConfigurationTable(SmtpConfiguration configuration) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(SmtpConfiguration.TABLE_SMTP_CONFIGURATION);

        if (configuration != null) {
            builder.newRow(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                    .with(SmtpConfiguration.COLUMN_HOST, configuration.getHost())
                    .with(SmtpConfiguration.COLUMN_PORT, configuration.getPort())
                    .with(SmtpConfiguration.COLUMN_SSL, configuration.isSsl())
                    .with(SmtpConfiguration.COLUMN_USERNAME, configuration.getUsername())
                    .with(SmtpConfiguration.COLUMN_PASSWORD, configuration.getPassword())
                    .with(SmtpConfiguration.COLUMN_USE_LOCAL_SETTING, configuration.isUseLocalSetting())
                    .with(SmtpConfiguration.COLUMN_ID, configuration.getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet domainConfigurationTable(UrlConfiguration urlConfiguration) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.ensureTableIsPresent(UrlConfiguration.URL_CONFIGURATION_TABLE);

        if (urlConfiguration != null) {
            builder.newRow(UrlConfiguration.URL_CONFIGURATION_TABLE)
                    .with(UrlConfiguration.ID_COLUMN, urlConfiguration.getId())
                    .with(UrlConfiguration.DOMAIN_COLUMN, urlConfiguration.getDomain())
                    .with(UrlConfiguration.SCHEME_COLUMN, urlConfiguration.getScheme())
                    .with(UrlConfiguration.PORT_COLUMN, urlConfiguration.getPort())
                    .add();
        }

        return builder.build();
    }

    public static void datasourceDbSetup(Datasource datasource) {
        DbSetup dbSetup = new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                insertInto(Datasource.TABLE)
                        .row()
                            .column(COLUMN_ID, datasource.getId())
                            .column(COLUMN_LABEL, datasource.getLabel())
                            .column(COLUMN_PASSWORD, datasource.getPassword())
                            .column(COLUMN_PORT, datasource.getPort())
                            .column(COLUMN_TYPE, datasource.getType())
                            .column(COLUMN_URL, datasource.getUrl())
                            .column(COLUMN_USERNAME, datasource.getUsername())
                            .column(COLUMN_DATABASE, datasource.getDatabase())
                        .end()
                        .build()
        );

        dbSetup.launch();
    }

    public static void sqlQueryDbSetUp(Collection<SqlQueryModel> ms) {
        for (SqlQueryModel m : ms) {
            sqlQueryDbSetUp(m);
        }
    }

    public static void sqlQueryDbSetUp(SqlQueryModel sqlQueryModel){
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                        .row()
                        .column(SqlQueryModel.ID_COLUMN, sqlQueryModel.getId())
                        .column(SqlQueryModel.LABEL_COLUMN, sqlQueryModel.getLabel())
                        .column(SqlQueryModel.QUERY_COLUMN, sqlQueryModel.getQuery())
                        .column(SqlQueryModel.TITLE_COLUMN, sqlQueryModel.getTitle())
                        .column(SqlQueryModel.DATASOURCE_ID_FK_COLUMN, sqlQueryModel.getDatasource().getId())
                        .end()
                        .build()
        ).launch();
    }

    public static void jobDbSetUp(JobModel jobModel) {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.insertInto(JOB_TABLE)
                        .row()
                        .column(JobModel.ID_COLUMN, jobModel.getId())
                        .column(JobModel.CRON_EXPRESSION_COLUMN, jobModel.getCronExpression())
                        .column(JobModel.NAME_COLUMN, jobModel.getName())
                        .column(JobModel.TITLE_COLUMN, jobModel.getTitle())
                        .end()
                        .build()
        ).launch();
    }

    public static void jobEmailDbSetUp(JobModel jobModel) {
        for (String email : jobModel.getEmails()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.insertInto(JobModel.JOB_EMAIL_TABLE)
                            .row()
                                .column(JobModel.JOB_EMAIL_ID_COLUMN, dbId())
                                .column(JobModel.JOB_EMAIL_TABLE_JOB_ID_FK_COLUMN, jobModel.getId())
                                .column(JobModel.JOB_EMAIL_TABLE_EMAIL_COLUMN, email)
                            .end()
                            .build()
            ).launch();
        }
    }

    public static void jobFailureAlertEmailDbSetUp(JobModel jobModel) {
        for (String email : jobModel.getFailureAlertEmails()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.insertInto(JobModel.JOB_FAILURE_ALERT_EMAIL_TABLE)
                            .row()
                            .column(JobModel.JOB_FAILURE_ALERT_EMAIL_ID_COLUMN, dbId())
                            .column(JobModel.JOB_FAILURE_ALERT_EMAIL_TABLE_JOB_ID_FK_COLUMN, jobModel.getId())
                            .column(JobModel.JOB_FAILURE_ALERT_EMAIL_TABLE_EMAIL_COLUMN, email)
                            .end()
                            .build()
            ).launch();
        }
    }

    public static void jobDbSetUp(List<JobModel> jobModels) {
        for (JobModel jobModel : jobModels) {
            jobDbSetUp(jobModel);
        }
    }

    public static void jobSqlQueryDbSetUp(JobModel jobModel) {
        int order = 0;
        for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.sequenceOf(
                            insertInto(JOB_SQL_QUERY_TABLE)
                                    .row()
                                    .column(JobModel.ID_COLUMN, dbId())
                                    .column(JobModel.JOB_ID_FK_COLUMN, jobModel.getId())
                                    .column(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                                    .column(JobModel.JOB_SQL_QUERY_TABLE_UI_ORDER_COLUMN, order)
                                    .end()
                                    .build()
                    )
            ).launch();
            order = order + 1;
        }
    }

    public static void jobDependentDbSetUp(JobModel jobModel) {
        for (JobModel dependentJobModel : jobModel.getChildJobs()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    sequenceOf(
                            insertInto(JOB_CHILDREN_TABLE)
                                    .row()
                                    .column(JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN, jobModel.getId())
                                    .column(JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN, dependentJobModel.getId())
                                    .end()
                                    .build()
                    )
            ).launch();
        }

        if (jobModel.getParentJob() != null) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    sequenceOf(
                            insertInto(JOB_CHILDREN_TABLE)
                                    .row()
                                    .column(JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN, jobModel.getParentJob().getId())
                                    .column(JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN, jobModel.getId())
                                    .end()
                                    .build()
                    )
            ).launch();
        }
    }

    public static void jobExecutionDbSetUp(JobExecutionModel jobExecutionModel) {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(JobExecutionModel.TABLE)
                                .row()
                                    .column(JobExecutionModel.COLUMN_ID, jobExecutionModel.getId())
                                    .column(JobExecutionModel.COLUMN_EXECUTION_ID, jobExecutionModel.getExecutionId())
                                    .column(JobExecutionModel.COLUMN_EXECUTION_START, jobExecutionModel.getExecutionStart())
                                    .column(JobExecutionModel.COLUMN_EXECUTION_END, jobExecutionModel.getExecutionEnd())
                                    .column(JobExecutionModel.COLUMN_STATUS, jobExecutionModel.getStatus())
                                    .column(JobExecutionModel.JOB_ID_FK_COLUMN, jobExecutionModel.getJobModel().getId())
                                .end()
                                .build()
                )
        ).launch();
    }

    public static void sqlQueryExecutionDbSetUp(SqlQueryExecutionModel model) {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .row()
                                .column(SqlQueryExecutionModel.COLUMN_ID, model.getId())
                                .column(SqlQueryExecutionModel.COLUMN_EXECUTION_ID, model.getExecutionId())
                                .column(SqlQueryExecutionModel.COLUMN_EXECUTION_START, model.getExecutionStart())
                                .column(SqlQueryExecutionModel.COLUMN_EXECUTION_END, model.getExecutionEnd())
                                .column(SqlQueryExecutionModel.COLUMN_RESULT, model.getResult())
                                .column(SqlQueryExecutionModel.COLUMN_STATUS, model.getStatus())
                                .column(SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK, model.getSqlQuery().getId())
                                .column(SqlQueryExecutionModel.COLUMN_JOB_EXECUTION_ID_FK, model.getJobExecutionModel().getId())
                                .end()
                                .build()
                )
        ).launch();
    }

    public static void userDbSetUp(User user) {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(User.TABLE_DASH_REPO_USER)
                                .row()
                                    .column(User.COLUMN_ID, user.getId())
                                    .column(User.COLUMN_USERNAME, user.getUsername())
                                    .column(User.COLUMN_PASSWORD, user.getPassword())
                                .end()
                                .build()
                )
        ).launch();
    }

    public static void jobLabelDbSetUp(JobLabelModel jobLabelModel) {
        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(JobLabelModel.JOB_LABEL_TABLE)
                                .row()
                                .column(JobLabelModel.ID_COLUMN, jobLabelModel.getId())
                                .column(JobLabelModel.LABEL_COLUMN, jobLabelModel.getLabel())
                                .column(JobLabelModel.PARENT_LABEL_ID_FK_COLUMN, jobLabelModel.getParentLabel() != null ? jobLabelModel.getParentLabel().getId() : null)
                                .end()
                                .build()
                )
        ).launch();
    }

    public static void jobJobLabelDbSetUp(JobModel jobModel) {
        for (JobLabelModel jobLabelModel : jobModel.getLabels()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    sequenceOf(
                            insertInto(JobModel.JOB_JOB_LABEL_TABLE)
                                    .row()
                                        .column(JobModel.JOB_JOB_LABEL_TABLE_ID_COLUMN, dbId())
                                        .column(JobModel.JOB_JOB_LABEL_TABLE_FK_JOB_ID_COLUMN, jobModel.getId())
                                        .column(JobModel.JOB_JOB_LABEL_TABLE_FK_JOB_LABEL_ID_COLUMN, jobLabelModel.getId())
                                    .end()
                                    .build()
                    )
            ).launch();
        }
    }

    public static void jobRuleDbSetUp(JobModel jobModel) {
        for (Map.Entry<Rules, String> rule : jobModel.getRules().entrySet()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    sequenceOf(
                            insertInto(JobModel.JOB_RULE_TABLE)
                                    .row()
                                    .column(JobModel.JOB_RULE_TABLE_ID_COLUMN, dbId())
                                    .column(JobModel.JOB_RULE_TABLE_NAME_COLUMN, rule.getKey())
                                    .column(JobModel.JOB_RULE_TABLE_VALUE_COLUMN, rule.getValue())
                                    .column(JobModel.JOB_RULE_JOB_ID_FK_COLUMN, jobModel.getId())
                                    .end()
                                    .build()
                    )
            ).launch();
        }
    }

    public static void smtpConfigurationDbSetUp(SmtpConfiguration smtpConfiguration) {
        new DbSetup(new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                                .row()
                                .column(SmtpConfiguration.COLUMN_ID, smtpConfiguration.getId())
                                .column(SmtpConfiguration.COLUMN_HOST, smtpConfiguration.getHost())
                                .column(SmtpConfiguration.COLUMN_PORT, smtpConfiguration.getPort())
                                .column(SmtpConfiguration.COLUMN_SSL, smtpConfiguration.isSsl())
                                .column(SmtpConfiguration.COLUMN_USERNAME, smtpConfiguration.getUsername())
                                .column(SmtpConfiguration.COLUMN_PASSWORD, smtpConfiguration.getPassword())
                                .column(SmtpConfiguration.COLUMN_USE_LOCAL_SETTING, smtpConfiguration.isUseLocalSetting())
                                .end()
                                .build()
                )
        ).launch();
    }

    public static void domainConfigurationDbSetUp(UrlConfiguration urlConfiguration) {
        new DbSetup(new DataSourceDestination(getDatasource()),
                insertInto(UrlConfiguration.URL_CONFIGURATION_TABLE)
                        .row()
                        .column(UrlConfiguration.ID_COLUMN, urlConfiguration.getId())
                        .column(UrlConfiguration.DOMAIN_COLUMN, urlConfiguration.getDomain())
                        .column(UrlConfiguration.SCHEME_COLUMN, urlConfiguration.getScheme())
                        .column(UrlConfiguration.PORT_COLUMN, urlConfiguration.getPort())
                        .end()
                        .build()
        ).launch();
    }

    public static int dbId() {
        return RandomUtils.nextInt(1, TestUtil.DB_START_ID + 1);
    }
}
