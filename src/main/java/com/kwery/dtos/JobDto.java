package com.kwery.dtos;

import java.util.List;

public class JobDto {
    protected int id;
    protected String title;
    protected String cronExpression;
    protected String label;
    protected List<SqlQueryDto> sqlQueries;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<SqlQueryDto> getSqlQueries() {
        return sqlQueries;
    }

    public void setSqlQueries(List<SqlQueryDto> sqlQueries) {
        this.sqlQueries = sqlQueries;
    }
}
