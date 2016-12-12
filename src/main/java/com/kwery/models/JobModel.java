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
    public static final String TITLE_COLUMN = "title";

    public static final String JOB_SQL_QUERY_TABLE = "job_sql_query";
    public static final String SQL_QUERY_ID_FK_COLUMN = "sql_query_id_fk";
    public static final String JOB_ID_FK_COLUMN = "job_id_fk";

    public static final String JOB_DEPENDENT_TABLE_ID_COLUMN = "id";
    public static final String JOB_DEPENDENT_TABLE = "job_dependent";
    public static final String JOB_DEPENDENT_TABLE_JOB_ID_FK_COLUMN = "job_id_fk";
    public static final String JOB_DEPENDENT_TABLE_DEPENDENT_JOB_ID_FK_COLUMN = "dependent_job_id_fk";

    public static final String JOB_EMAIL_TABLE = "job_email";
    public static final String JOB_EMAIL_ID_COLUMN = "id";
    public static final String JOB_EMAIL_TABLE_JOB_ID_FK_COLUMN = "job_id_fk";
    public static final String JOB_EMAIL_TABLE_EMAIL_COLUMN = "email";

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

    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = TITLE_COLUMN)
    private String title;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinTable(
            name = JOB_SQL_QUERY_TABLE,
            joinColumns = @JoinColumn(name = JOB_ID_FK_COLUMN, referencedColumnName = ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = SQL_QUERY_ID_FK_COLUMN, referencedColumnName = SqlQueryModel.ID_COLUMN)
    )
    public Set<SqlQueryModel> sqlQueries;

    @ManyToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = JOB_DEPENDENT_TABLE,
            joinColumns = @JoinColumn(name = JOB_DEPENDENT_TABLE_JOB_ID_FK_COLUMN, referencedColumnName = JobModel.ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = JOB_DEPENDENT_TABLE_DEPENDENT_JOB_ID_FK_COLUMN, referencedColumnName = JobModel.ID_COLUMN)
    )
    public Set<JobModel> dependentJobs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = JOB_EMAIL_TABLE,
            joinColumns = @JoinColumn(name = JOB_EMAIL_TABLE_JOB_ID_FK_COLUMN)
    )
    @Column(name = JOB_EMAIL_TABLE_EMAIL_COLUMN)
    private Set<String> emails;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<SqlQueryModel> getSqlQueries() {
        return sqlQueries;
    }

    public void setSqlQueries(Set<SqlQueryModel> sqlQueries) {
        this.sqlQueries = sqlQueries;
    }

    public Set<JobModel> getDependentJobs() {
        return dependentJobs;
    }

    public void setDependentJobs(Set<JobModel> dependentJobs) {
        this.dependentJobs = dependentJobs;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> recipientEmails) {
        this.emails = recipientEmails;
    }

    @Override
    public String toString() {
        return "JobModel{" +
                "id=" + id +
                ", cronExpression='" + cronExpression + '\'' +
                ", label='" + label + '\'' +
                ", title='" + title + '\'' +
                ", sqlQueries=" + sqlQueries +
                ", dependentJobs=" + dependentJobs +
                ", emails=" + emails +
                '}';
    }
}
