package com.kwery.models;

import javax.persistence.*;

@Entity
@Table(name = JobRuleModel.JOB_RULE_TABLE)
public class JobRuleModel {
    public static final String JOB_RULE_TABLE = "jjob_rule";
    public static final String JOB_RULE_ID_COLUMN = "id";
    public static final String SEQUENTIAL_SQL_QUERY_EXECUTION_COLUMN = "sequential_sql_query_execution";
    public static final String STOP_EXECUTION_ON_SQL_QUERY_FAILURE_COLUMN = "stop_execution_on_sql_query_failure";

    public static final String JOB_JOB_RULE_TABLE = "job_job_rule";
    public static final String JOB_JOB_RULE_ID_COLUMN = "id";
    public static final String JOB_ID_FK_COLUMN = "job_id_fk";
    public static final String JOB_RULE_ID_FK_COLUMN = "job_rule_id_fk";

    @Column(name = JOB_RULE_ID_COLUMN)
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    protected Integer id;

    @Column(name = SEQUENTIAL_SQL_QUERY_EXECUTION_COLUMN)
    protected boolean sequentialSqlQueryExecution;

    @Column(name = STOP_EXECUTION_ON_SQL_QUERY_FAILURE_COLUMN)
    protected boolean stopExecutionOnSqlQueryFailure;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isSequentialSqlQueryExecution() {
        return sequentialSqlQueryExecution;
    }

    public void setSequentialSqlQueryExecution(boolean sequentialSqlQueryExecution) {
        this.sequentialSqlQueryExecution = sequentialSqlQueryExecution;
    }

    public boolean isStopExecutionOnSqlQueryFailure() {
        return stopExecutionOnSqlQueryFailure;
    }

    public void setStopExecutionOnSqlQueryFailure(boolean stopExecutionOnSqlQueryFailure) {
        this.stopExecutionOnSqlQueryFailure = stopExecutionOnSqlQueryFailure;
    }
}
