package views;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class ActionResult {
    private Status status;
    private List<String> messages;

    public ActionResult() {
    }

    public ActionResult(Status status, String message) {
        this.status = status;
        this.messages = ImmutableList.of(message);
    }

    public ActionResult(Status status, List<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public enum Status{
        success,
        failure
    }
}
