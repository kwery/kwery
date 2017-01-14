package com.kwery.dtos;

public class JobLabelDto {
    protected String labelName;
    protected int labelId;
    protected int parentLabelId;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public int getParentLabelId() {
        return parentLabelId;
    }

    public void setParentLabelId(int parentLabelId) {
        this.parentLabelId = parentLabelId;
    }
}
