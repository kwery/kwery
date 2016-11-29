package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = JobModel.JOB_TABLE)
public class JobModel {
    public static final String JOB_TABLE = "job";
    public static final String ID_COLUMN = "id";
    public static final String CRON_EXPRESSION_COLUMN = "cron_expression";
    public static final String LABEL_COLUMN = "label";

    public static final String JOB_SQL_QUERY_TABLE = "job_sql_query";
    public static final String SQL_QUERY_ID_FK_COLUMN = "sql_query_id_fk";
    public static final String JOB_ID_FK_COLUMN = "job_id_fk";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_COLUMN)
    public Integer id;

    @Column(name = CRON_EXPRESSION_COLUMN)
    @Size(max = 255)
    public String cronExpression;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = LABEL_COLUMN)
    public String label;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinTable(
            name = JOB_SQL_QUERY_TABLE,
            joinColumns = @JoinColumn(name = JOB_ID_FK_COLUMN, referencedColumnName = ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = SQL_QUERY_ID_FK_COLUMN, referencedColumnName = SqlQueryModel.ID_COLUMN)
    )
    public Set<SqlQueryModel> sqlQueries;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Set<SqlQueryModel> getSqlQueries() {
        return sqlQueries;
    }

    public void setSqlQueries(Set<SqlQueryModel> sqlQueries) {
        this.sqlQueries = sqlQueries;
    }

    @Override
    public String toString() {
        return "JobModel{" +
                "id=" + id +
                ", cronExpression='" + cronExpression + '\'' +
                ", label='" + label + '\'' +
                ", sqlQueries=" + sqlQueries +
                '}';
    }
}
