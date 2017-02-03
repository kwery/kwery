package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = UrlConfiguration.URL_CONFIGURATION_TABLE)
public class UrlConfiguration {
    public static final String URL_CONFIGURATION_TABLE = "url_configuration";
    public static final String ID_COLUMN = "id";
    public static final String DOMAIN_COLUMN = "domain";
    public static final String SCHEME_COLUMN = "scheme";
    public static final String PORT_COLUMN = "port";

    public static final int DOMAIN_MIN = 4;
    public static final int DOMAIN_MAX = 253;
    public static final int PORT_MIN = 1;
    public static final int PORT_MAX = 65535;

    @Column(name = ID_COLUMN)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;

    @Column(name = DOMAIN_COLUMN)
    @Size(min = DOMAIN_MIN, max = DOMAIN_MAX)
    protected String domain;

    @Column(name = SCHEME_COLUMN)
    @Enumerated(STRING)
    protected Scheme scheme;

    @Column(name = PORT_COLUMN)
    @Min(PORT_MIN)
    @Max(PORT_MAX)
    protected Integer port;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "UrlConfiguration{" +
                "id=" + id +
                ", domain='" + domain + '\'' +
                ", scheme='" + scheme + '\'' +
                ", port=" + port +
                '}';
    }

    public enum Scheme {
        http, https
    }
}
