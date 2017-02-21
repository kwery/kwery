package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = KweryVersionModel.KWERY_VERSION_MODEL_TABLE)
public class KweryVersionModel {
    public static final String KWERY_VERSION_MODEL_TABLE = "kwery_version";
    public static final String ID_COLUMN = "id";
    public static final String VERSION_COLUMN = "version";
    public static final int VERSION_COLUMN_LENGTH_MIN = 5;
    public static final int VERSION_COLUMN_LENGTH_MAX = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_COLUMN)
    protected Integer id;

    @NotNull
    @Size(min = VERSION_COLUMN_LENGTH_MIN, max = VERSION_COLUMN_LENGTH_MAX)
    @Column(name = VERSION_COLUMN)
    protected String version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "KweryVersionModel{" +
                "id=" + id +
                ", version='" + version + '\'' +
                '}';
    }
}
