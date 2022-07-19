package com.wallscope.pronombackend.model;

public class Feedback {
    public enum Status {SUCCESS, INFO, ALERT, ERROR}

    private final Status status;
    private final String message;

    public Feedback(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatusText() {
        if(status == null) return null;
        return switch (status) {
            case SUCCESS -> "Success";
            case INFO -> "Information";
            case ALERT -> "Warning";
            case ERROR -> "Error";
        };
    }

    public String getCssClass() {
        if(status == null) return null;
        return switch (status) {
            case SUCCESS -> "green";
            case INFO -> "blue";
            case ALERT -> "yellow";
            case ERROR -> "red";
        };
    }

    public String getIcon() {
        if(status == null) return null;
        return switch (status) {
            case SUCCESS -> "fa-circle-check";
            case INFO -> "fa-circle-info";
            case ALERT -> "fa-triangle-exclamation";
            case ERROR -> "fa-circle-xmark";
        };
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
