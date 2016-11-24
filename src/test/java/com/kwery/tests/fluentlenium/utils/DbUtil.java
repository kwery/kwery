package com.kwery.tests.fluentlenium.utils;

import com.google.common.io.Resources;
import com.mchange.v2.c3p0.C3P0Registry;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import static com.kwery.models.User.COLUMN_ID;
import static com.kwery.models.User.COLUMN_PASSWORD;
import static com.kwery.models.User.COLUMN_USERNAME;
import static com.kwery.models.User.TABLE_DASH_REPO_USER;
import static org.dbunit.Assertion.assertEquals;

public class DbUtil {
    public static Operation userTable() {
        return Operations.insertInto(TABLE_DASH_REPO_USER)
                .columns(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD)
                .values("1", "root", "root").build();
    }

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
        IDatabaseConnection connection = new DatabaseConnection(DbUtil.getDatasource().getConnection());
        try {
            IDataSet databaseDataSet = connection.createDataSet();
            ITable actualTable = databaseDataSet.getTable(tableName);
            ITable expectedTable = expectedDataSet.getTable(tableName);
            assertEquals(expectedTable, actualTable);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
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

            assertEquals(expectedTable, actualTable);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
