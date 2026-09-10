package com.airtribe.meditrack.exception;

public class BillNotFoundException extends Exception {

    public BillNotFoundException(String message) {
        super(message);
    }

    public BillNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}