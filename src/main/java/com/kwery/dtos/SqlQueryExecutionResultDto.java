package com.kwery.dtos;

import com.kwery.models.SqlQueryExecutionModel;

import java.util.List;

public class SqlQueryExecutionResultDto {
    protected String title;
    protected List<String[]> jsonResult;
    protected SqlQueryExecutionModel.Status status;
    protected String errorResult;
    protected String executionId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String[]> getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(List<String[]> jsonResult) {
        this.jsonResult = jsonResult;
    }

    public SqlQueryExecutionModel.Status getStatus() {
        return status;
    }

    public void setStatus(SqlQueryExecutionModel.Status status) {
        this.status = status;
    }

    public String getErrorResult() {
        return errorResult;
    }

    public void setErrorResult(String errorResult) {
        this.errorResult = errorResult;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}
