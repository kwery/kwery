package com.kwery.dtos;

import java.util.List;
import java.util.Set;

public class JobDto {
    protected int id;
    protected String title;
    protected String cronExpression;
    protected String name;
    protected int parentJobId;
    protected Set<String> emails;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentJobId() {
        return parentJobId;
    }

    public void setParentJobId(int parentJobId) {
        this.parentJobId = parentJobId;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public List<SqlQueryDto> getSqlQueries() {
        return sqlQueries;
    }

    public void setSqlQueries(List<SqlQueryDto> sqlQueries) {
        this.sqlQueries = sqlQueries;
    }
}
