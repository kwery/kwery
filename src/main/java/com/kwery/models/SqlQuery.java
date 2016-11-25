package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = SqlQuery.TABLE)
public class SqlQuery {
    public static final String TABLE = "query_run";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_QUERY = "query";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_CRON_EXPRESSION = "cron_expression";
    public static final String COLUMN_DATASOURCE_ID_FK = "datasource_id_fk";

    public static final String TABLE_QUERY_RUN_DEPENDENT = "query_run_dependent";
    public static final String COLUMN_QUERY_RUN_ID_FK = "query_run_id_fk";
    public static final String COLUMN_DEPENDENT_QUERY_RUN_ID_FK = "dependent_query_run_id_fk";

    public static final String TABLE_QUERY_RUN_EMAIL_RECIPIENT = "query_run_email_recipient";
    public static final String COLUMN_EMAIL = "email";

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
    private String cronExpression;

    @JoinColumn(name = COLUMN_DATASOURCE_ID_FK)
    @ManyToOne
    @NotNull
    private Datasource datasource;

    //TODO - Fetch type can be lazy
    @ManyToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = TABLE_QUERY_RUN_DEPENDENT,
            joinColumns = @JoinColumn(name = COLUMN_QUERY_RUN_ID_FK, referencedColumnName = SqlQuery.COLUMN_ID),
            inverseJoinColumns = @JoinColumn(name = COLUMN_DEPENDENT_QUERY_RUN_ID_FK, referencedColumnName = SqlQuery.COLUMN_ID)
    )
    private List<SqlQuery> dependentQueries;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = TABLE_QUERY_RUN_EMAIL_RECIPIENT,
            joinColumns = @JoinColumn(name = COLUMN_QUERY_RUN_ID_FK)
    )
    @Column(name = COLUMN_EMAIL)
    private Set<String> recipientEmails;

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

    public List<SqlQuery> getDependentQueries() {
        return dependentQueries;
    }

    public void setDependentQueries(List<SqlQuery> dependentQueries) {
        this.dependentQueries = dependentQueries;
    }

    public Set<String> getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(Set<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    @Override
    public String toString() {
        return "SqlQuery{" +
                "id=" + id +
                ", query='" + query + '\'' +
                ", label='" + label + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", datasource=" + datasource +
                ", dependentQueries=" + dependentQueries +
                ", recipientEmails=" + recipientEmails +
                '}';
    }
}
