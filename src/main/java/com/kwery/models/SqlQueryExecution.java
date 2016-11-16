package com.kwery.models;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = SqlQueryExecution.TABLE)
public class SqlQueryExecution {
    public static final String TABLE = "query_run_execution";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EXECUTION_ID = "execution_id";
    public static final String COLUMN_EXECUTION_START = "execution_start";
    public static final String COLUMN_EXECUTION_END = "execution_end";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_QUERY_RUN_ID_FK = "query_run_id_fk";

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

    @Column(name = COLUMN_RESULT)
    @Type(type = "text")
    private String result;

    @JoinColumn(name = COLUMN_QUERY_RUN_ID_FK)
    @ManyToOne
    private SqlQuery sqlQuery;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public SqlQuery getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(SqlQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public static enum Status {
        ONGOING, SUCCESS, FAILURE, KILLED
    }
}
