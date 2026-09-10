package com.airtribe.meditrack.exception;

public class BillNotFoundException extends RuntimeException {

    BillNotFoundException(String message) {
        super(message);
    }
}
