package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "query_run_execution")
public class QueryRunExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String executionId;
    private Long executionStart;
    private Long executionEnd;
    private Status status;
    private String result;
    @ManyToOne
    @JoinColumn(name = "query_run_id_fk")
    private QueryRun queryRun;

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

    public QueryRun getQueryRun() {
        return queryRun;
    }

    public void setQueryRun(QueryRun queryRun) {
        this.queryRun = queryRun;
    }

    public static enum Status {
        ONGOING, SUCCESS, FAILURE, KILLED
    }
}
