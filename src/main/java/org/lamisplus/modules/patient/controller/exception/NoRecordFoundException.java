package org.lamisplus.modules.patient.controller.exception;

public class NoRecordFoundException extends RuntimeException {
    public NoRecordFoundException(String message) {
        super (message);
    }
}
