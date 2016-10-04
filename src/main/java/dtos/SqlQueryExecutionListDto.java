package dtos;

import java.util.List;

public class SqlQueryExecutionListDto {
    private String sqlQuery;
    private List<SqlQueryExecutionDto> sqlQueryExecutionDtos;

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public List<SqlQueryExecutionDto> getSqlQueryExecutionDtos() {
        return sqlQueryExecutionDtos;
    }

    public void setSqlQueryExecutionDtos(List<SqlQueryExecutionDto> sqlQueryExecutionDtos) {
        this.sqlQueryExecutionDtos = sqlQueryExecutionDtos;
    }
}
