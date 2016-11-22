package com.kwery.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = EmailConfiguration.TABLE_EMAIL_CONFIGURATION)
public class EmailConfiguration {
    public static final String TABLE_EMAIL_CONFIGURATION = "email_configuration";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BCC = "bcc";
    public static final String COLUMN_REPLY_TO = "reply_to";

    @Column(name = COLUMN_ID)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    @Column(name = COLUMN_BCC)
    public String bcc;

    @Column(name = COLUMN_REPLY_TO)
    public String replyTo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public String toString() {
        return "EmailConfiguration{" +
                "id=" + id +
                ", bcc='" + bcc + '\'' +
                ", replyTo='" + replyTo + '\'' +
                '}';
    }
}
