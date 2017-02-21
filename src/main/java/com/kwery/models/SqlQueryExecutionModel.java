package com.kwery.models;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = SqlQueryExecutionModel.TABLE)
public class SqlQueryExecutionModel {
    public static final String TABLE = "sql_query_execution";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EXECUTION_ID = "execution_id";
    public static final String COLUMN_EXECUTION_START = "execution_start";
    public static final String COLUMN_EXECUTION_END = "execution_end";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_EXECUTION_ERROR = "execution_error";
    public static final String COLUMN_QUERY_RUN_ID_FK = "sql_query_id_fk";
    public static final String COLUMN_JOB_EXECUTION_ID_FK = "job_execution_id_fk";
    public static final String COLUMN_RESULT_FILE_NAME = "result_file_name";

    public static final int COLUMN_RESULT_FILE_NAME_LENGTH = 36;

    @Column(name = COLUMN_ID)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = COLUMN_EXECUTION_ID)
    private String executionId;

    @Column(name = COLUMN_EXECUTION_START)
    private Long executionStart;

    @Column(name = COLUMN_EXECUTION_END)
    private Long executionEnd;

    @Column(name = COLUMN_STATUS)
    @Enumerated(STRING)
    private Status status;

    @Column(name = COLUMN_EXECUTION_ERROR)
    @Type(type = "text")
    private String executionError;

    @Column(name = COLUMN_RESULT_FILE_NAME)
    @Size(min = COLUMN_RESULT_FILE_NAME_LENGTH, max = COLUMN_RESULT_FILE_NAME_LENGTH)
    private String resultFileName;

    @JoinColumn(name = COLUMN_QUERY_RUN_ID_FK)
    @ManyToOne
    private SqlQueryModel sqlQuery;

    @ManyToOne
    @JoinColumn(name = SqlQueryExecutionModel.COLUMN_JOB_EXECUTION_ID_FK)
    private JobExecutionModel jobExecutionModel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public Long getExecutionStart() {
        return executionStart;
    }

    public void setExecutionStart(Long start) {
        this.executionStart = start;
    }

    public Long getExecutionEnd() {
        return executionEnd;
    }

    public void setExecutionEnd(Long end) {
        this.executionEnd = end;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getExecutionError() {
        return executionError;
    }

    public void setExecutionError(String executionError) {
        this.executionError = executionError;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public SqlQueryModel getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(SqlQueryModel sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public JobExecutionModel getJobExecutionModel() {
        return jobExecutionModel;
    }

    public void setJobExecutionModel(JobExecutionModel jobExecutionModel) {
        this.jobExecutionModel = jobExecutionModel;
    }

    public enum Status {
        ONGOING, SUCCESS, FAILURE, KILLED
    }
}
