package services.scheduler;

import java.sql.ResultSet;

public interface ResultSetProcessorFactory {
    public ResultSetProcessor create(ResultSet resultSet);
}
