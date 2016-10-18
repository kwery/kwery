package dtos;

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

    public static enum Action {
        ADD_ADMIN_USER, ADD_DATASOURCE, ADD_SQL_QUERY, SHOW_RUNNING_QUERIES
    }
}

