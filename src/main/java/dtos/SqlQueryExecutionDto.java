package dtos;

public class SqlQueryExecutionDto {
    private String sqlQueryLabel;
    private String sqlQueryExecutionStartTime;
    private String datasourceLabel;
    private Integer sqlQueryId;
    private String sqlQueryExecutionId;

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

    public Integer getSqlQueryId() {
        return sqlQueryId;
    }

    public void setSqlQueryId(Integer sqlQueryId) {
        this.sqlQueryId = sqlQueryId;
    }

    public String getSqlQueryExecutionId() {
        return sqlQueryExecutionId;
    }

    public void setSqlQueryExecutionId(String sqlQueryExecutionId) {
        this.sqlQueryExecutionId = sqlQueryExecutionId;
    }
}
