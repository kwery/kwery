package com.kwery.models;

import javax.persistence.*;
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
    public static final String TITLE_COLUMN = "title";

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

    @Column(name = TITLE_COLUMN)
    @NotNull
    @Size(min = 1, max = 1024)
    private String title;

    @JoinColumn(name = DATASOURCE_ID_FK_COLUMN)
    @ManyToOne
    @NotNull
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
                ", title='" + title + '\'' +
                ", datasource=" + datasource +
                '}';
    }
}
