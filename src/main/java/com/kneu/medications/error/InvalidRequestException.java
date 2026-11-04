// "Your request is bad" - the error handler turns this into a 400.
// Used by: PrescriptionService (throws it), ApiExceptionHandler (catches it).
package com.kneu.medications.error;

/** Maps to HTTP 400. */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
