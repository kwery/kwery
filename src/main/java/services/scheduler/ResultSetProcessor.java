package services.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ResultSetProcessor {
    private final ResultSet resultSet;

    @Inject
    public ResultSetProcessor(@Assisted ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public String process() throws SQLException, JsonProcessingException {
        List<List<String>> result = new LinkedList<>();

        ResultSetMetaData rsmd = resultSet.getMetaData();

        int count = rsmd.getColumnCount();

        List<String> labels = new ArrayList<>(count);

        for (int i = 1; i <= count; ++i) {
            labels.add(rsmd.getColumnLabel(i));
        }

        result.add(labels);

        while (resultSet.next()) {
            List<String> values = new ArrayList<>(count);
            for (int i = 1; i <= count; ++i) {
                values.add(resultSet.getString(i));
            }
            result.add(values);
        }

        return new ObjectMapper().writeValueAsString(result);
    }
}
