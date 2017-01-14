package com.kwery.tests.fluentlenium.job.save;

import com.kwery.dtos.JobDto;

import java.util.List;

public class JobForm extends JobDto {
    protected boolean useCronUi;

    public boolean isUseCronUi() {
        return useCronUi;
    }

    public void setUseCronUi(boolean useCronUi) {
        this.useCronUi = useCronUi;
    }
}
