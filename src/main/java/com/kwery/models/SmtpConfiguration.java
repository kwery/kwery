package com.kwery.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
public class SmtpConfiguration {
    public static final String TABLE_SMTP_CONFIGURATION = "smtp_configuration";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HOST = "host";
    public static final String COLUMN_PORT = "port";
    public static final String COLUMN_SSL = "ssl";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    @Column(name = COLUMN_ID)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    @Column(name = COLUMN_HOST)
    public String host;

    @Column(name = COLUMN_PORT)
    public Integer port;

    @Column(name = COLUMN_SSL)
    public boolean ssl;

    @Column(name = COLUMN_USERNAME)
    public String username;

    @Column(name = COLUMN_PASSWORD)
    public String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
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

    @Override
    public String toString() {
        return "SmtpConfiguration{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", ssl=" + ssl +
                ", username='" + username + '\'' +
                ", password='" + "X" + '\'' +
                '}';
    }
}
