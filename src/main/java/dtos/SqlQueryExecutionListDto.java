package dtos;

import java.util.List;

public class SqlQueryExecutionListDto {
    private String sqlQuery;
    private List<SqlQueryExecutionDto> sqlQueryExecutionDtos;
    private long totalCount;

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

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
