// "The thing you asked for doesn't exist" - the error handler turns this into a 404.
// Used by: PrescriptionService (throws it), ApiExceptionHandler (catches it).
package com.kneu.medications.error;

/** Maps to HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
