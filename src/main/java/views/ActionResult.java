package views;

public class ActionResult {
    private Status status;
    private String message;

    public ActionResult() {
    }

    public ActionResult(Status status, String message) {
        this.status = status;
        this.message = message;
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

    @Override
    public String toString() {
        return "ActionResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

    public enum Status{
        success,
        failure
    }
}
