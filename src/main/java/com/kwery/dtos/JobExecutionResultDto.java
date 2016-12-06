package com.kwery.dtos;

import java.util.List;

public class JobExecutionResultDto {
    protected String title;
    protected List<SqlQueryExecutionResultDto> sqlQueryExecutionResultDtos;

    public JobExecutionResultDto(String title, List<SqlQueryExecutionResultDto> sqlQueryExecutionResultDtos) {
        this.title = title;
        this.sqlQueryExecutionResultDtos = sqlQueryExecutionResultDtos;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SqlQueryExecutionResultDto> getSqlQueryExecutionResultDtos() {
        return sqlQueryExecutionResultDtos;
    }

    public void setSqlQueryExecutionResultDtos(List<SqlQueryExecutionResultDto> sqlQueryExecutionResultDtos) {
        this.sqlQueryExecutionResultDtos = sqlQueryExecutionResultDtos;
    }
}
