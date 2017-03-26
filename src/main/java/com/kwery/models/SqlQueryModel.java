package com.kwery.models;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.kwery.models.SqlQueryEmailSettingModel.*;
import static org.hibernate.search.annotations.Index.YES;

@Entity
@Table(name = SqlQueryModel.SQL_QUERY_TABLE)
public class SqlQueryModel {
    public static final String SQL_QUERY_TABLE = "sql_query";
    public static final String ID_COLUMN = "id";
    public static final String QUERY_COLUMN = "query";
    public static final String LABEL_COLUMN = "label";
    public static final String DATASOURCE_ID_FK_COLUMN = "datasource_id_fk";
    public static final String TITLE_COLUMN = "title";

    public static final int QUERY_MAX_LENGTH = 32672;
    public static final int QUERY_MIN_LENGTH = 1;

    @Column(name = ID_COLUMN)
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = QUERY_COLUMN)
    @Size(min = QUERY_MIN_LENGTH, max = QUERY_MAX_LENGTH, message = "query.validation")
    @NotNull(message = "query.validation")
    private String query;

    @Column(name = LABEL_COLUMN, unique = true)
    @NotNull(message = "label.validation")
    @Size(min = 1, max = 1024)
    @Field(index= YES, analyze= Analyze.YES, store= Store.NO)
    private String label;

    @Column(name = TITLE_COLUMN)
    @NotNull
    @Size(min = 1, max = 1024)
    @Field(index= YES, analyze= Analyze.YES, store= Store.NO)
    private String title;

    @ManyToOne
    @JoinColumn(name = DATASOURCE_ID_FK_COLUMN)
    @NotNull
    @IndexedEmbedded(depth = 1)
    private Datasource datasource;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinTable(
            name = SQL_QUERY_SQL_QUERY_EMAIL_SETTING_TABLE,
            joinColumns = @JoinColumn(name = SQL_QUERY_ID_FK_COLUMN, referencedColumnName = ID_COLUMN),
            inverseJoinColumns = @JoinColumn(name = SQL_QUERY_EMAIL_SETTING_ID_FK_COLUMN, referencedColumnName = SQL_QUERY_EMAIL_SETTING_ID_COLUMN)
    )
    private SqlQueryEmailSettingModel sqlQueryEmailSettingModel;

    //If we try to delete a SqlQueryModel from a report and if that SqlQueryModel has executions, delete fails. Hence adding this here, so that JPA can manage the delete
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sqlQuery")
    private List<SqlQueryExecutionModel> sqlQueryExecutionModels;

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

    public SqlQueryEmailSettingModel getSqlQueryEmailSettingModel() {
        return sqlQueryEmailSettingModel;
    }

    public void setSqlQueryEmailSettingModel(SqlQueryEmailSettingModel sqlQueryEmailSettingModel) {
        this.sqlQueryEmailSettingModel = sqlQueryEmailSettingModel;
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
