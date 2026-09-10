package com.airtribe.meditrack.exception;

public class PaymentNotFoundException extends RuntimeException{
    PaymentNotFoundException(String message){
        super(message);
    }
}
