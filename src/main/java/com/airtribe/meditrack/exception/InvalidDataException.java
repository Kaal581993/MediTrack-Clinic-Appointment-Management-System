package com.airtribe.meditrack.exception;

public class InvalidDataException extends RuntimeException{
    InvalidDataException(String message){
        throw new RuntimeException(message);
    }
}
