package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = COLUMN_ID)
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = COLUMN_URL)
    @Size(min = 1, message = "url.validation")
    @NotNull(message = "url.validation")
    private String url;

    @Column(name = COLUMN_PORT)
    @Min(value = 1, message = "port.validation")
    @NotNull(message = "port.validation")
    private Integer port;

    @Column(name = COLUMN_USERNAME)
    @Size(min = 1, message = "username.validation")
    @NotNull(message = "username.validation")
    private String username;

    //TODO - Should be excluded from toString or from JSON serialization
    @Column(name = COLUMN_PASSWORD)
    private String password;

    @Column(name = COLUMN_LABEL, unique = true)
    @Size(min = 1, message = "label.validation")
    @NotNull(message = "label.validation")
    private String label;

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        MYSQL
    }

    //TODO - Mask password
    @Override
    public String toString() {
        return "Datasource{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", label='" + label + '\'' +
                ", type=" + type +
                '}';
    }
}
