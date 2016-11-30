package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = SqlQueryModel.SQL_QUERY_TABLE)
public class SqlQueryModel {
    public static final String SQL_QUERY_TABLE = "sql_query";
    public static final String ID_COLUMN = "id";
    public static final String QUERY_COLUMN = "query";
    public static final String LABEL_COLUMN = "label";
    public static final String CRON_EXPRESSION_COLUMN = "cron_expression";
    public static final String DATASOURCE_ID_FK_COLUMN = "datasource_id_fk";

    public static final String TABLE_QUERY_RUN_DEPENDENT = "query_run_dependent";
    public static final String COLUMN_QUERY_RUN_ID_FK = "query_run_id_fk";
    public static final String COLUMN_DEPENDENT_QUERY_RUN_ID_FK = "dependent_query_run_id_fk";

    public static final String TABLE_QUERY_RUN_EMAIL_RECIPIENT = "query_run_email_recipient";
    public static final String COLUMN_EMAIL = "email";

    @Column(name = ID_COLUMN)
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = QUERY_COLUMN)
    @Size(min = 1, message = "query.validation")
    @NotNull(message = "query.validation")
    private String query;

    @Column(name = LABEL_COLUMN, unique = true)
    @NotNull(message = "label.validation")
    private String label;

    @Transient
    private String cronExpression;

    @JoinColumn(name = DATASOURCE_ID_FK_COLUMN)
    @ManyToOne
    @NotNull
    private Datasource datasource;

/*    //TODO - Fetch type can be lazy
    @ManyToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = TABLE_QUERY_RUN_DEPENDENT,
            joinColumns = @JoinColumn(name = COLUMN_QUERY_RUN_ID_FK, referencedColumnName = SqlQueryModel.ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = COLUMN_DEPENDENT_QUERY_RUN_ID_FK, referencedColumnName = SqlQueryModel.ID_COLUMN)
    )*/
    @Transient
    private List<SqlQueryModel> dependentQueries;

/*    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = TABLE_QUERY_RUN_EMAIL_RECIPIENT,
            joinColumns = @JoinColumn(name = COLUMN_QUERY_RUN_ID_FK)
    )
    @Column(name = COLUMN_EMAIL)*/
    @Transient
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

    public List<SqlQueryModel> getDependentQueries() {
        return dependentQueries;
    }

    public void setDependentQueries(List<SqlQueryModel> dependentQueries) {
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
        return "SqlQueryModel{" +
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
