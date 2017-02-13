package com.kwery.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = JobModel.JOB_TABLE)
public class JobModel {
    public static final String JOB_TABLE = "job";
    public static final String ID_COLUMN = "id";
    public static final String CRON_EXPRESSION_COLUMN = "cron_expression";
    public static final String NAME_COLUMN = "j_name";
    public static final String TITLE_COLUMN = "title";

    public static final String JOB_SQL_QUERY_TABLE = "job_sql_query";
    public static final String SQL_QUERY_ID_FK_COLUMN = "sql_query_id_fk";
    public static final String JOB_ID_FK_COLUMN = "job_id_fk";
    public static final String JOB_SQL_QUERY_TABLE_ID_COLUMN = "id";
    public static final String JOB_SQL_QUERY_TABLE_UI_ORDER_COLUMN = "ui_order";

    public static final String JOB_CHILDREN_TABLE_ID_COLUMN = "id";
    public static final String JOB_CHILDREN_TABLE = "job_children";
    public static final String JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN = "parent_job_id_fk";
    public static final String JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN = "child_job_id_fk";

    public static final String JOB_EMAIL_TABLE = "job_email";
    public static final String JOB_EMAIL_ID_COLUMN = "id";
    public static final String JOB_EMAIL_TABLE_JOB_ID_FK_COLUMN = "job_id_fk";
    public static final String JOB_EMAIL_TABLE_EMAIL_COLUMN = "email";

    public static final String JOB_JOB_LABEL_TABLE = "job_job_label";
    public static final String JOB_JOB_LABEL_TABLE_ID_COLUMN = "id";
    public static final String JOB_JOB_LABEL_TABLE_FK_JOB_ID_COLUMN = "job_id_fk";
    public static final String JOB_JOB_LABEL_TABLE_FK_JOB_LABEL_ID_COLUMN = "job_label_id_fk";

    public static final String JOB_RULE_TABLE = "job_rule";
    public static final String JOB_RULE_TABLE_ID_COLUMN = "id";
    public static final String JOB_RULE_TABLE_NAME_COLUMN = "r_name";
    public static final String JOB_RULE_TABLE_VALUE_COLUMN = "value";
    public static final String JOB_RULE_JOB_ID_FK_COLUMN = "job_id_fk";

    public static final String JOB_FAILURE_ALERT_EMAIL_TABLE = "job_failure_alert_email";
    public static final String JOB_FAILURE_ALERT_EMAIL_ID_COLUMN = "id";
    public static final String JOB_FAILURE_ALERT_EMAIL_TABLE_JOB_ID_FK_COLUMN = "job_id_fk";
    public static final String JOB_FAILURE_ALERT_EMAIL_TABLE_EMAIL_COLUMN = "email";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_COLUMN)
    protected Integer id;

    @Column(name = CRON_EXPRESSION_COLUMN)
    @Size(max = 255)
    protected String cronExpression;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = NAME_COLUMN)
    protected String name;

    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = TITLE_COLUMN)
    protected String title;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = JOB_JOB_LABEL_TABLE,
            joinColumns = @JoinColumn(name = JOB_JOB_LABEL_TABLE_FK_JOB_ID_COLUMN, referencedColumnName = ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = JOB_JOB_LABEL_TABLE_FK_JOB_LABEL_ID_COLUMN, referencedColumnName = JobLabelModel.ID_COLUMN)
    )
    protected Set<JobLabelModel> labels = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinTable(
            name = JOB_SQL_QUERY_TABLE,
            joinColumns = @JoinColumn(name = JOB_ID_FK_COLUMN, referencedColumnName = ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = SQL_QUERY_ID_FK_COLUMN, referencedColumnName = SqlQueryModel.ID_COLUMN)
    )
    @OrderColumn(name = JobModel.JOB_SQL_QUERY_TABLE_UI_ORDER_COLUMN)
    protected List<SqlQueryModel> sqlQueries = new LinkedList<>();

    @ManyToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = JOB_CHILDREN_TABLE,
            joinColumns = @JoinColumn(name = JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN, referencedColumnName = JobModel.ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN, referencedColumnName = JobModel.ID_COLUMN)
    )
    protected Set<JobModel> childJobs;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = JOB_CHILDREN_TABLE,
            joinColumns = @JoinColumn(name = JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN, referencedColumnName = JobModel.ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = JOB_CHILDREN_TABLE_PARENT_JOB_ID_FK_COLUMN, referencedColumnName = JobModel.ID_COLUMN)
    )
    protected JobModel parentJob;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = JOB_EMAIL_TABLE,
            joinColumns = @JoinColumn(name = JOB_EMAIL_TABLE_JOB_ID_FK_COLUMN)
    )
    @Column(name = JOB_EMAIL_TABLE_EMAIL_COLUMN)
    protected Set<String> emails;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = JOB_FAILURE_ALERT_EMAIL_TABLE,
            joinColumns = @JoinColumn(name = JOB_FAILURE_ALERT_EMAIL_TABLE_JOB_ID_FK_COLUMN)
    )
    @Column(name = JOB_FAILURE_ALERT_EMAIL_TABLE_EMAIL_COLUMN)
    protected Set<String> failureAlertEmails = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name= JOB_RULE_TABLE_NAME_COLUMN)
    @Column(name= JOB_RULE_TABLE_VALUE_COLUMN)
    @CollectionTable(name=JOB_RULE_TABLE, joinColumns=@JoinColumn(name= JOB_RULE_JOB_ID_FK_COLUMN))
    @MapKeyEnumerated(STRING)
    protected Map<Rules, String> rules = new HashMap<>();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinTable(
            name = JobRuleModel.JOB_JOB_RULE_TABLE,
            joinColumns = @JoinColumn(name = JobRuleModel.JOB_ID_FK_COLUMN, referencedColumnName = ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = JobRuleModel.JOB_RULE_ID_FK_COLUMN, referencedColumnName = JobModel.ID_COLUMN)
    )
    protected JobRuleModel jobRuleModel;

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

    public String getName() {
        return name;
    }

    public void setName(String label) {
        this.name = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<JobLabelModel> getLabels() {
        return labels;
    }

    public void setLabels(Set<JobLabelModel> labels) {
        this.labels = labels;
    }

    public List<SqlQueryModel> getSqlQueries() {
        return sqlQueries;
    }

    public void setSqlQueries(List<SqlQueryModel> sqlQueries) {
        this.sqlQueries = sqlQueries;
    }

    public Set<JobModel> getChildJobs() {
        return childJobs;
    }

    public void setChildJobs(Set<JobModel> childJobs) {
        this.childJobs = childJobs;
    }

    public JobModel getParentJob() {
        return parentJob;
    }

    public void setParentJob(JobModel parentJob) {
        this.parentJob = parentJob;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> recipientEmails) {
        this.emails = recipientEmails;
    }

    public Set<String> getFailureAlertEmails() {
        return failureAlertEmails;
    }

    public void setFailureAlertEmails(Set<String> failureAlertEmails) {
        this.failureAlertEmails = failureAlertEmails;
    }

    public Map<Rules, String> getRules() {
        return rules;
    }

    public void setRules(Map<Rules, String> rules) {
        this.rules = rules;
    }

    public enum Rules {
        EMPTY_REPORT_NO_EMAIL
    }

    public JobRuleModel getJobRuleModel() {
        return jobRuleModel;
    }

    public void setJobRuleModel(JobRuleModel jobRuleModel) {
        this.jobRuleModel = jobRuleModel;
    }
}
