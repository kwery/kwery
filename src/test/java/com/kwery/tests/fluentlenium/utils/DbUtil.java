package com.kwery.tests.fluentlenium.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.kwery.models.*;
import com.mchange.v2.c3p0.C3P0Registry;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
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
import java.util.Set;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobModel.JOB_TABLE;
import static com.ninja_squad.dbsetup.Operations.insertInto;
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

    public static IDataSet jobTable(List<JobModel> ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();

        for (JobModel m : ms) {
            builder.newRow(JOB_TABLE)
                    .with(JobModel.LABEL_COLUMN, m.getLabel())
                    .with(JobModel.CRON_EXPRESSION_COLUMN, m.getCronExpression())
                    .with(JobModel.TITLE_COLUMN, m.getTitle())
                    .with(JobModel.ID_COLUMN, m.getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet jobDependentTable(JobModel jobModel) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();

        for (JobModel m : jobModel.getDependentJobs()) {
            builder.newRow(JobModel.JOB_DEPENDENT_TABLE)
                    .with(JobModel.JOB_DEPENDENT_TABLE_JOB_ID_FK_COLUMN, jobModel.getId())
                    .with(JobModel.JOB_DEPENDENT_TABLE_DEPENDENT_JOB_ID_FK_COLUMN, m.getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet sqlQueryTable(SqlQueryModel m) throws DataSetException {
        return sqlQueryTable(ImmutableList.of(m));
    }

    public static IDataSet sqlQueryTable(Collection<SqlQueryModel> ms) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();

        for (SqlQueryModel m : ms) {
            builder.newRow(SqlQueryModel.SQL_QUERY_TABLE)
                    .with(SqlQueryModel.ID_COLUMN, m.getId())
                    .with(SqlQueryModel.LABEL_COLUMN, m.getLabel())
                    .with(SqlQueryModel.QUERY_COLUMN, m.getQuery())
                    .with(SqlQueryModel.DATASOURCE_ID_FK_COLUMN, m.getDatasource().getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet jobSqlQueryTable(JobModel jobModel) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();

        for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
            builder.newRow(JobModel.JOB_SQL_QUERY_TABLE)
                    .with(JobModel.JOB_ID_FK_COLUMN, jobModel.getId())
                    .with(JobModel.SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                    .add();
        }

        return builder.build();
    }

    public static IDataSet jobExecutionTable(JobExecutionModel jobExecutionModel) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(JobExecutionModel.TABLE)
                .with(JobExecutionModel.COLUMN_ID, jobExecutionModel.getId())
                .with(JobExecutionModel.COLUMN_EXECUTION_ID, jobExecutionModel.getExecutionId())
                .with(JobExecutionModel.COLUMN_EXECUTION_START, jobExecutionModel.getExecutionStart())
                .with(JobExecutionModel.COLUMN_EXECUTION_END, jobExecutionModel.getExecutionEnd())
                .with(JobExecutionModel.COLUMN_STATUS, jobExecutionModel.getStatus())
                .with(JobExecutionModel.JOB_ID_FK_COLUMN, jobExecutionModel.getJobModel().getId())
                .add();

        return builder.build();
    }

    public static IDataSet sqlQueryExecutionTable(SqlQueryExecutionModel model) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();

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

        return builder.build();
    }

    public static void datasourceDbSetup(Datasource datasource) {
        DbSetup dbSetup = new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                insertInto(Datasource.TABLE)
                        .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                        .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType(), datasource.getUrl(), datasource.getUsername())
                        .build()
        );

        dbSetup.launch();
    }

    public static void sqlQueryDbSetUp(SqlQueryModel sqlQueryModel){
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                        .row()
                        .column(SqlQueryModel.ID_COLUMN, sqlQueryModel.getId())
                        .column(SqlQueryModel.LABEL_COLUMN, sqlQueryModel.getLabel())
                        .column(SqlQueryModel.QUERY_COLUMN, sqlQueryModel.getQuery())
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
                        .column(JobModel.LABEL_COLUMN, jobModel.getLabel())
                        .column(JobModel.TITLE_COLUMN, jobModel.getTitle())
                        .end()
                        .build()
        ).launch();
    }

    public static void jobDbSetUp(List<JobModel> jobModels) {
        for (JobModel jobModel : jobModels) {
            jobDbSetUp(jobModel);
        }
    }
}
