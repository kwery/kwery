package com.kwery.dtos;

public class LicenseDto {
    protected boolean license;
    protected boolean trialPeriod;

    public boolean isLicense() {
        return license;
    }

    public void setLicense(boolean license) {
        this.license = license;
    }

    public boolean isTrialPeriod() {
        return trialPeriod;
    }

    public void setTrialPeriod(boolean trialPeriod) {
        this.trialPeriod = trialPeriod;
    }
}
