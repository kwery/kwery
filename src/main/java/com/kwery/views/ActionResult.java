package com.kwery.views;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public class ActionResult {
    private Status status;
    //Generic messages go here
    private List<String> messages;
    //Messages associated with a field go here
    private Map<String, List<String>> fieldMessages;

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

    public ActionResult(Status status, Map<String, List<String>> fieldMessages) {
        this.status = status;
        this.fieldMessages = fieldMessages;
    }

    public ActionResult(Status status, List<String> messages, Map<String, List<String>> fieldMessages) {
        this.status = status;
        this.messages = messages;
        this.fieldMessages = fieldMessages;
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

    public Map<String, List<String>> getFieldMessages() {
        return fieldMessages;
    }

    public void setFieldMessages(Map<String, List<String>> fieldMessages) {
        this.fieldMessages = fieldMessages;
    }

    public enum Status{
        success,
        failure
    }
}
