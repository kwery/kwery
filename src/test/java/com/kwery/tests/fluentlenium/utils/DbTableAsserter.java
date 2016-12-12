package com.kwery.tests.fluentlenium.utils;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static org.dbunit.Assertion.assertEquals;

public class DbTableAsserter {
    private final String table;
    private final IDataSet expectedDataSet;
    private final Set<String> columnsToIgnore;
    private final Set<String> columnsToCompare;

    private DbTableAsserter(DbTableAsserterBuilder b) {
        this.table = b.table;
        this.expectedDataSet = b.expectedDataSet;
        this.columnsToIgnore = b.columnsToIgnore;
        this.columnsToCompare = b.columnsToCompare;
    }

    public void assertTable() throws SQLException, DatabaseUnitException, IOException {
        IDatabaseConnection connection = new DatabaseConnection(getDatasource().getConnection());
        try {
            IDataSet databaseDataSet = connection.createDataSet();
            ITable actualTable = databaseDataSet.getTable(table);
            ITable expectedTable = expectedDataSet.getTable(table);

            if (!columnsToIgnore.isEmpty()) {
                String[] toIgnore = new ArrayList<>(columnsToIgnore).toArray(new String[columnsToIgnore.size()]);

                actualTable = DefaultColumnFilter.excludedColumnsTable(actualTable, toIgnore);
                expectedTable = DefaultColumnFilter.excludedColumnsTable(expectedTable, toIgnore);
            }

            if (!columnsToCompare.isEmpty()) {
                String[] toCompare = new ArrayList<>(columnsToCompare).toArray(new String[columnsToCompare.size()]);

                actualTable = new SortedTable(actualTable, toCompare);
                ((SortedTable)actualTable).setUseComparable(true);

                expectedTable = new SortedTable(new TableWrapper(expectedTable, actualTable.getTableMetaData()), toCompare);
                ((SortedTable)expectedTable).setUseComparable(true);
            } else {
                actualTable = new SortedTable(actualTable);
                ((SortedTable)actualTable).setUseComparable(true);

                expectedTable = new SortedTable(new TableWrapper(expectedTable, actualTable.getTableMetaData()));
                ((SortedTable)expectedTable).setUseComparable(true);
            }

/*            System.out.println("Expected==");
            System.out.println("0 => " + ((SortedTable)expectedTable).getValue(0, "id"));
            System.out.println("1 => " + ((SortedTable)expectedTable).getValue(1, "id"));

            System.out.println("Actual==");
            System.out.println("0 => " + ((SortedTable)actualTable).getValue(0, "id"));
            System.out.println("1 => " + ((SortedTable)actualTable).getValue(1, "id"));*/

            System.out.println("Expected => " + expectedTable);
            System.out.println("Actual => " + actualTable);

            assertEquals(expectedTable, actualTable);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static class DbTableAsserterBuilder {
        private String table;
        private IDataSet expectedDataSet;
        private Set<String> columnsToIgnore = new HashSet<>();
        private Set<String> columnsToCompare = new HashSet<>();

        public DbTableAsserterBuilder(String table, IDataSet expectedDataSet) {
            this.table = table;
            this.expectedDataSet = expectedDataSet;
        }

        public DbTableAsserterBuilder columnToIgnore(String column) {
            this.columnsToIgnore.add(column);
            return this;
        }

        public DbTableAsserterBuilder columnsToIgnore(String... columns) {
            for (String column : columns) {
                this.columnToIgnore(column);
            }
            return this;
        }

        public DbTableAsserterBuilder columnToCompare(String column) {
            this.columnsToCompare.add(column);
            return this;
        }

        public DbTableAsserterBuilder columnsToCompare(String... columns) {
            for (String column : columns) {
                this.columnToCompare(column);
            }
            return this;
        }

        public DbTableAsserter build() {
            return new DbTableAsserter(this);
        }
    }
}
