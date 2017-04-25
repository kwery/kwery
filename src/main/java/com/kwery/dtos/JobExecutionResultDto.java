package com.kwery.dtos;

import com.kwery.models.JobExecutionModel;

import java.util.List;

public class JobExecutionResultDto {
    protected String title;
    protected List<SqlQueryExecutionResultDto> sqlQueryExecutionResultDtos;
    protected JobExecutionModel.Status executionStatus;

    public JobExecutionResultDto(String title, List<SqlQueryExecutionResultDto> sqlQueryExecutionResultDtos, JobExecutionModel.Status status) {
        this.title = title;
        this.sqlQueryExecutionResultDtos = sqlQueryExecutionResultDtos;
        this.executionStatus = status;
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

    public JobExecutionModel.Status getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(JobExecutionModel.Status executionStatus) {
        this.executionStatus = executionStatus;
    }
}
