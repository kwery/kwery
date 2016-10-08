package fluentlenium.utils;

import com.google.common.io.Resources;
import com.mchange.v2.c3p0.C3P0Registry;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import static models.User.COLUMN_ID;
import static models.User.COLUMN_PASSWORD;
import static models.User.COLUMN_USERNAME;
import static models.User.TABLE_DASH_REPO_USER;

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

            Assertion.assertEquals(expectedTable, actualTable);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
