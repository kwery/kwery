package com.kwery.dtos;

public class OnboardingNextActionDto {
    protected Action action;

    public OnboardingNextActionDto(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public enum Action {
        ADD_DATASOURCE, ADD_JOB, SHOW_HOME_SCREEN, LOGIN, SIGN_UP
    }
}

