package views;

public class ActionResult {
    private Status status;
    private String message;
    private String nextActionName;
    private String nextAction;

    public ActionResult() {
    }

    public ActionResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public ActionResult(Status status, String message, String nextActionName, String nextAction) {
        this.status = status;
        this.message = message;
        this.nextActionName = nextActionName;
        this.nextAction = nextAction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNextActionName() {
        return nextActionName;
    }

    public void setNextActionName(String nextActionName) {
        this.nextActionName = nextActionName;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", nextActionName='" + nextActionName + '\'' +
                ", nextAction='" + nextAction + '\'' +
                '}';
    }

    public enum Status{
        success,
        failure
    }
}
