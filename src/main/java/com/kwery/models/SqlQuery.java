package com.kwery.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = SqlQuery.TABLE)
public class SqlQuery {
    public static final String TABLE = "query_run";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_QUERY = "query";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_CRON_EXPRESSION = "cron_expression";
    public static final String COLUMN_DATASOURCE_ID_FK = "datasource_id_fk";

    @Column(name = COLUMN_ID)
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = COLUMN_QUERY)
    @Size(min = 1, message = "query.validation")
    @NotNull(message = "query.validation")
    private String query;

    @Column(name = COLUMN_LABEL, unique = true)
    @NotNull(message = "label.validation")
    private String label;

    @Column(name = COLUMN_CRON_EXPRESSION)
    @Size(min = 1, message = "cron.expression.validation")
    @NotNull(message = "cron.expression.validation")
    private String cronExpression;

    @JoinColumn(name = COLUMN_DATASOURCE_ID_FK)
    @ManyToOne
    @NotNull
    private Datasource datasource;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
    }
}
