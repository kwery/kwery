package views;

public class ActionResult {
    public ActionResult(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    private String message;
    private boolean status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
