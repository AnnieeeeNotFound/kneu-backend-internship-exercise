// Shape of the JSON a client sends to POST /prescriptions - just a data box, no logic.
// Calls: nothing. Used by: PrescriptionController (receives it), PrescriptionService (validates it).
package com.kneu.medications.prescription;

/**
 * Body for POST /prescriptions.
 */
public record CreatePrescriptionRequest(Long medicationId, java.time.LocalTime timeOfDay, String label) {
}
