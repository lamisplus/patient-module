package org.lamisplus.modules.patient.controller.exception;

public class AlreadyExistException extends RuntimeException {

    public AlreadyExistException(String message) {
        super (message);
    }

}
