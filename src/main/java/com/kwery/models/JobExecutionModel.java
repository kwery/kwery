package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = JobExecutionModel.TABLE)
public class JobExecutionModel {
    public static final String TABLE = "job_execution";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EXECUTION_ID = "execution_id";
    public static final String COLUMN_EXECUTION_START = "execution_start";
    public static final String COLUMN_EXECUTION_END = "execution_end";
    public static final String COLUMN_STATUS = "status";
    public static final String JOB_ID_FK_COLUMN = "job_id_fk";

    @Column(name = COLUMN_ID)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = COLUMN_EXECUTION_ID)
    @Size(min = 1, max = 255)
    private String executionId;

    @Column(name = COLUMN_EXECUTION_START)
    private Long executionStart;

    @Column(name = COLUMN_EXECUTION_END)
    private Long executionEnd;

    @Column(name = COLUMN_STATUS)
    @Enumerated(STRING)
    private Status status;

    @JoinColumn(name = JOB_ID_FK_COLUMN)
    @ManyToOne
    private JobModel jobModel;

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

    public JobModel getJobModel() {
        return jobModel;
    }

    public void setJobModel(JobModel jobModel) {
        this.jobModel = jobModel;
    }

    public enum Status {
        ONGOING, SUCCESS, FAILURE, KILLED
    }

    @Override
    public String toString() {
        return "JobExecutionModel{" +
                "id=" + id +
                ", executionId='" + executionId + '\'' +
                ", executionStart=" + executionStart +
                ", executionEnd=" + executionEnd +
                ", status=" + status +
                ", jobModel=" + jobModel +
                '}';
    }
}
