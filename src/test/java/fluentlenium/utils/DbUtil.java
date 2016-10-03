package fluentlenium.utils;

import com.mchange.v2.c3p0.C3P0Registry;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

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
}
