package com.kwery.dtos;

import com.kwery.models.SqlQueryExecutionModel;

import java.util.List;

public class SqlQueryExecutionResultDto {
    public SqlQueryExecutionResultDto(String title, SqlQueryExecutionModel.Status status, List<List<?>> resultJson) {
        this.title = title;
        this.jsonResult = resultJson;
        this.status = status;
    }

    public SqlQueryExecutionResultDto(String title, SqlQueryExecutionModel.Status status, String errorResult) {
        this.title = title;
        this.status = status;
        this.errorResult = errorResult;
    }

    protected String title;
    protected List<List<?>> jsonResult;
    protected SqlQueryExecutionModel.Status status;
    protected String errorResult;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<List<?>> getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(List<List<?>> jsonResult) {
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
}
