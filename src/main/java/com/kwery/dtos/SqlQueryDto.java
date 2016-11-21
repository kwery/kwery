package com.kwery.dtos;

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

    private String cronExpression;

    @Min(value = 1, message= "datasource.validation")
    @NotNull(message = "datasource.validation")
    private Integer datasourceId;

    private Integer dependsOnSqlQueryId;

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

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public Integer getDependsOnSqlQueryId() {
        return dependsOnSqlQueryId;
    }

    public void setDependsOnSqlQueryId(Integer dependsOnSqlQueryId) {
        this.dependsOnSqlQueryId = dependsOnSqlQueryId;
    }
}
