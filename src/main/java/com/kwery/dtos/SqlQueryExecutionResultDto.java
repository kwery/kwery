package com.kwery.dtos;

import java.util.List;

public class SqlQueryExecutionResultDto {
    public SqlQueryExecutionResultDto(String title, List<List<?>> resultJson) {
        this.title = title;
        this.jsonResult = resultJson;
    }

    protected String title;
    protected List<List<?>> jsonResult;

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
}
