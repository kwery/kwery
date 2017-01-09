package com.kwery.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = JobLabelModel.JOB_LABEL_TABLE)
public class JobLabelModel {
    public static final String JOB_LABEL_TABLE = "job_label";
    public static final String ID_COLUMN  = "id";
    public static final String LABEL_COLUMN = "label";
    public static final String PARENT_LABEL_ID_FK_COLUMN = "parent_label_id_fk";

    public static final int LABEL_MIN_LENGTH = 1;
    public static final int LABEL_MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_COLUMN)
    protected Integer id;

    @Column(name = LABEL_COLUMN)
    @Size(min = LABEL_MIN_LENGTH, max = LABEL_MAX_LENGTH)
    @NotNull
    protected String label;

    @OneToMany(mappedBy = "parentLabel", fetch = FetchType.EAGER)
    protected Set<JobLabelModel> childLabels;

    @JsonIgnore /*To prevent stack overflow error while serializing this into JSON*/
    @ManyToOne
    @JoinColumn(name = PARENT_LABEL_ID_FK_COLUMN)
    protected JobLabelModel parentLabel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<JobLabelModel> getChildLabels() {
        return childLabels;
    }

    public void setChildLabels(Set<JobLabelModel> childLabels) {
        this.childLabels = childLabels;
    }

    public JobLabelModel getParentLabel() {
        return parentLabel;
    }

    public void setParentLabel(JobLabelModel parentLabel) {
        this.parentLabel = parentLabel;
    }
}
