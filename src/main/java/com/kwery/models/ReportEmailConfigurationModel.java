package com.kwery.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = ReportEmailConfigurationModel.REPORT_EMAIL_CONFIGURATION_TABLE)
public class ReportEmailConfigurationModel extends AbstractBaseModel {
    public static final String REPORT_EMAIL_CONFIGURATION_TABLE = "report_email_configuration";
    public static final String LOGO_URL_COLUMN = "logo_url";

    @NotNull
    @Column(name = LOGO_URL_COLUMN)
    protected String logoUrl;

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}