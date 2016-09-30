package dtos;

public class SqlQueryExecutionDto {
    private String sqlQueryLabel;
    private String sqlQueryExecutionStartTime;
    private String datasourceLabel;

    public String getSqlQueryLabel() {
        return sqlQueryLabel;
    }

    public void setSqlQueryLabel(String sqlQueryLabel) {
        this.sqlQueryLabel = sqlQueryLabel;
    }

    public String getSqlQueryExecutionStartTime() {
        return sqlQueryExecutionStartTime;
    }

    public void setSqlQueryExecutionStartTime(String sqlQueryExecutionStartTime) {
        this.sqlQueryExecutionStartTime = sqlQueryExecutionStartTime;
    }

    public String getDatasourceLabel() {
        return datasourceLabel;
    }

    public void setDatasourceLabel(String datasourceLabel) {
        this.datasourceLabel = datasourceLabel;
    }
}
