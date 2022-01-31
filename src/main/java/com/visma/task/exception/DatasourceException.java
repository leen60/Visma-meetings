package com.visma.task.exception;

public class DatasourceException extends RuntimeException {
    public DatasourceException(Throwable cause) {
        super(cause);
    }

    public DatasourceException(String message, Throwable cause) {
        super(message, cause);
    }
}