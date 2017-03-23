package com.kwery.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = InstallationDetailModel.INSTALLATION_DETAILS_TABLE)
public class InstallationDetailModel {
    public static final String INSTALLATION_DETAILS_TABLE = "installation_detail";

    public static final String INSTALLATION_EPOCH_COLUMN = "installation_epoch";
    public static final String ID_COLUMN = "id";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = ID_COLUMN)
    private Integer id;

    @NotNull
    @Column(name = INSTALLATION_EPOCH_COLUMN)
    private Long installationEpoch;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getInstallationEpoch() {
        return installationEpoch;
    }

    public void setInstallationEpoch(Long installationEpoch) {
        this.installationEpoch = installationEpoch;
    }
}
