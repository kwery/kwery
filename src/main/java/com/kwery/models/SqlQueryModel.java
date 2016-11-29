package com.kwery.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = SqlQueryModel.SQL_QUERY_TABLE)
public class SqlQueryModel {
    public static final String SQL_QUERY_TABLE = "sql_query";
    public static final String ID_COLUMN = "id";
    public static final String QUERY_COLUMN = "query";
    public static final String LABEL_COLUMN = "label";
    public static final String DATASOURCE_ID_FK_COLUMN = "datasource_id_fk";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_COLUMN)
    private Integer id;

    @NotNull(message = "query.validation")
    @Size(min = 1, max = 256, message = "query.validation")
    @Column(name = QUERY_COLUMN)
    private String query;

    @NotNull(message = "label.validation")
    @Size(max = 256)
    @Column(name = LABEL_COLUMN, unique = true)
    private String label;

    @ManyToOne
    @NotNull
    @JoinColumn(name = DATASOURCE_ID_FK_COLUMN)
    private Datasource datasource;

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

    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public String toString() {
        return "SqlQueryModel{" +
                "id=" + id +
                ", query='" + query + '\'' +
                ", label='" + label + '\'' +
                ", datasource=" + datasource +
                '}';
    }
}

