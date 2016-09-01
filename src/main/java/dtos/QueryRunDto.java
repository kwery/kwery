package dtos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class QueryRunDto {
    @Size(min = 1, message = "query.validation")
    @NotNull(message = "query.validation")
    private String query;

    @NotNull(message = "label.validation")
    private String label;

    @Size(min = 1, message = "cron.expression.validation")
    @NotNull(message = "cron.expression.validation")
    private String cronExpression;

    @Min(value = 1, message= "cron.expression.datasource.validation")
    @NotNull(message = "cron.expression.validation")
    private Integer datasourceId;

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
}
