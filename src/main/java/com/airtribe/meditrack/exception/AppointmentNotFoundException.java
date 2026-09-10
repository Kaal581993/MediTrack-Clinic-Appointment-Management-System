package com.airtribe.meditrack.exception;

public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(String message) {
        super(message);
    }

    public AppointmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AppointmentNotFoundException forId(int appointmentId) {
        return new AppointmentNotFoundException("No appointment found with id: " + appointmentId);
    }
}
