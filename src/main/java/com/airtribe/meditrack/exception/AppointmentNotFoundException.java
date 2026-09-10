package com.airtribe.meditrack.exception;

/**
 * Thrown when an appointment is requested by id but no such appointment exists.
 * <p>
 * Unchecked, because a lookup miss is a programming/usage error at the call site
 * rather than a recoverable condition the caller is expected to declare.
 * <p>
 * Note the constructors call {@code super(message)} rather than throwing from inside
 * the constructor — throwing from a constructor would discard this type entirely and
 * surface a plain {@link RuntimeException} to the caller, so {@code catch
 * (AppointmentNotFoundException e)} would never match.
 */
public class AppointmentNotFoundException extends RuntimeException {

    /**
     * @param message description of what was looked up and not found
     */
    public AppointmentNotFoundException(String message) {
        super(message);
    }

    /**
     * Chaining constructor — preserves the original failure as the cause.
     *
     * @param message description of what was looked up and not found
     * @param cause   the underlying failure
     */
    public AppointmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenience factory for the common "no appointment with this id" case.
     *
     * @param appointmentId the id that was not found
     * @return a ready-to-throw exception carrying a consistent message
     */
    public static AppointmentNotFoundException forId(int appointmentId) {
        return new AppointmentNotFoundException("No appointment found with id: " + appointmentId);
    }
}
