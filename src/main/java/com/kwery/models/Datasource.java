package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = Datasource.TABLE)
public class Datasource {
    public static final String TABLE = "datasource";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_PORT = "port";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATABASE = "database";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = COLUMN_ID)
    private Integer id;

    @NotNull(message = "url.validation")
    @Size(min = 1, max = 255, message = "url.validation")
    @Column(name = COLUMN_URL)
    private String url;

    @Column(name = COLUMN_PORT)
    @Min(value = 1, message = "port.validation")
    @Max(value = 65565, message = "port.validation")
    @NotNull(message = "port.validation")
    private Integer port;

    @NotNull(message = "username.validation")
    @Size(min = 1, max = 255, message = "username.validation")
    @Column(name = COLUMN_USERNAME)
    private String username;

    @Size(max = 255)
    @Column(name = COLUMN_PASSWORD)
    private String password;

    @NotNull(message = "label.validation")
    @Size(min = 1, max = 255, message = "label.validation")
    @Column(name = COLUMN_LABEL, unique = true)
    private String label;

    @Column(name = COLUMN_DATABASE)
    @Size(max = 255)
    private String database;

    @Enumerated(STRING)
    @Column(name = COLUMN_TYPE)
    private Type type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        MYSQL, POSTGRESQL
    }

    @Override
    public String toString() {
        return "Datasource{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + "X" + '\'' +
                ", label='" + label + '\'' +
                ", database='" + database + '\'' +
                ", type=" + type +
                '}';
    }
}
