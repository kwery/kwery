package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = DomainSetting.DOMAIN_SETTING_TABLE)
public class DomainSetting {
    public static final String DOMAIN_SETTING_TABLE = "domain_setting";
    public static final String ID_COLUMN = "id";
    public static final String DOMAIN_COLUMN = "domain";
    public static final String SCHEME_COLUMN = "scheme";
    public static final String PORT_COLUMN = "port";

    public static final int DOMAIN_MIN = 4;
    public static final int DOMAIN_MAX = 253;
    public static final int SCHEME_MIN = 4;
    public static final int SCHEME_MAX = 5;
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
        return "DomainSetting{" +
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
