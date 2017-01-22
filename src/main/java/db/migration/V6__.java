package db.migration;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class V6__ implements JdbcMigration {
    @Override
    public void migrate(Connection connection) throws Exception {
        List<Integer> jobIds = jobIds(connection);

        for (Integer jobId : jobIds) {
            int order = 0;
            for (Integer sqlQueryId : sqlQueryIds(connection, jobId)) {
                try (PreparedStatement ps = connection.prepareStatement("update job_sql_query set ui_order = ? where job_id_fk = ? and sql_query_id_fk = ?")) {
                    ps.setInt(1, order);
                    ps.setInt(2, jobId);
                    ps.setInt(3, sqlQueryId);
                    ps.executeUpdate();
                    order = order + 1;
                }
            }
        }
    }

    private List<Integer> jobIds(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("select distinct job_id_fk as jobId from job_sql_query")) {
            //Get  distinct job ids from job_sql_query table
            ResultSet rs = ps.executeQuery();
            List<Integer> jobIds = new LinkedList<>();
            while (rs.next()) {
                jobIds.add(rs.getInt("jobId"));
            }
            return jobIds;
        }
    }

    private List<Integer> sqlQueryIds(Connection connection, int jobId) throws SQLException {
        try (PreparedStatement jobIdPreparedStatement = connection.prepareStatement("select sql_query_id_fk as sqlQueryId from job_sql_query where job_id = ?")) {
            jobIdPreparedStatement.setInt(1, jobId);

            ResultSet jobIdResultSet = jobIdPreparedStatement.executeQuery();
            List<Integer> sqlQueryIds = new LinkedList<>();
            while (jobIdResultSet.next()) {
                sqlQueryIds.add(jobIdResultSet.getInt("sqlQueryId"));
            }
            return sqlQueryIds;
        }
    }
}
