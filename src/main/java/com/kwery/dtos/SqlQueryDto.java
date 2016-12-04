package com.kwery.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SqlQueryDto {
    private int id;

    @Size(min = 1, message = "query.validation")
    @NotNull(message = "query.validation")
    private String query;

    @NotNull(message = "label.validation")
    @Size(min = 1, message = "label.validation")
    private String label;

    @JsonIgnore
    private String cronExpression;

    @Min(value = 1, message= "datasource.validation")
    @NotNull(message = "datasource.validation")
    private int datasourceId;

    @JsonIgnore
    private Integer dependsOnSqlQueryId;

    @JsonIgnore
    private String recipientEmailsCsv;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public int getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId) {
        this.datasourceId = datasourceId;
    }

    public Integer getDependsOnSqlQueryId() {
        return dependsOnSqlQueryId;
    }

    public void setDependsOnSqlQueryId(Integer dependsOnSqlQueryId) {
        this.dependsOnSqlQueryId = dependsOnSqlQueryId;
    }

    public String getRecipientEmailsCsv() {
        return recipientEmailsCsv;
    }

    public void setRecipientEmailsCsv(String recipientEmailsCsv) {
        this.recipientEmailsCsv = recipientEmailsCsv;
    }
}
