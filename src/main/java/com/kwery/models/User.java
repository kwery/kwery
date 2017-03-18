package com.kwery.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = User.TABLE_DASH_REPO_USER)
public class User extends AbstractBaseModel {
    public static final String TABLE_DASH_REPO_USER = "kwery_user";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_MIDDLE_NAME = "middle_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "email";

    public static final int FIRST_NAME_MAX = 255;
    public static final int FIRST_NAME_MIN = 1;

    public static final int MIDDLE_NAME_MAX = 255;
    public static final int MIDDLE_NAME_MIN = 1;

    public static final int LAST_NAME_MAX = 255;
    public static final int LAST_NAME_MIN = 1;

    public static final int EMAIL_MAX = 1024;
    public static final int EMAIL_MIN = 7;

    @Column(name = COLUMN_FIRST_NAME)
    @NotNull
    @Size(min = FIRST_NAME_MIN, max = FIRST_NAME_MAX)
    private String firstName;

    @Column(name = COLUMN_MIDDLE_NAME)
    @Size(min = MIDDLE_NAME_MIN, max = MIDDLE_NAME_MAX)
    private String middleName;

    @Column(name = COLUMN_LAST_NAME)
    @NotNull
    @Size(min = LAST_NAME_MIN, max = LAST_NAME_MAX)
    private String lastName;

    @Column(name = COLUMN_PASSWORD)
    @NotNull(message = "password.validation")
    @Size(min = 1, message = "password.validation")
    private String password;

    @Column(name = COLUMN_EMAIL, unique = true)
    @NotNull
    @Size(min = EMAIL_MIN, max = EMAIL_MAX)
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + "X" + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
