package com.kwery.models;

import javax.persistence.*;

@Entity
@Table(name = SqlQueryEmailSettingModel.SQL_QUERY_EMAIL_SETTING_TABLE)
public class SqlQueryEmailSettingModel {
    public static final String SQL_QUERY_EMAIL_SETTING_TABLE = "sql_query_email_setting";
    public static final String SQL_QUERY_EMAIL_SETTING_ID_COLUMN = "id";
    public static final String EMAIL_BODY_INCLUDE_COLUMN = "email_body_include";
    public static final String EMAIL_ATTACHMENT_INCLUDE_COLUMN = "email_attachment_include";

    public static final String SQL_QUERY_SQL_QUERY_EMAIL_SETTING_TABLE = "sql_query_sql_query_email_setting";
    public static final String SQL_QUERY_SQL_QUERY_EMAIL_SETTING_ID_COLUMN = "id";
    public static final String SQL_QUERY_ID_FK_COLUMN = "sql_query_id_fk";
    public static final String SQL_QUERY_EMAIL_SETTING_ID_FK_COLUMN = "sql_query_email_setting_id_fk";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = SQL_QUERY_EMAIL_SETTING_ID_COLUMN)
    protected Integer id;

    @Column(name = EMAIL_BODY_INCLUDE_COLUMN)
    protected boolean includeInEmailBody;

    @Column(name = EMAIL_ATTACHMENT_INCLUDE_COLUMN)
    protected boolean includeInEmailAttachment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getIncludeInEmailBody() {
        return includeInEmailBody;
    }

    public void setIncludeInEmailBody(boolean includeInEmailBody) {
        this.includeInEmailBody = includeInEmailBody;
    }

    public boolean getIncludeInEmailAttachment() {
        return includeInEmailAttachment;
    }

    public void setIncludeInEmailAttachment(boolean includeInEmailAttachment) {
        this.includeInEmailAttachment = includeInEmailAttachment;
    }
}
