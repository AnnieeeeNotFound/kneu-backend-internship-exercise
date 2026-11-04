// Shape of the JSON we send back for one prescription (label left out when null).
// Calls: Prescription (via from()). Used by: PrescriptionController.
package com.kneu.medications.prescription;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * What the API sends back for a prescription (label omitted when null).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PrescriptionResponse(Long id, Long medicationId, String medicationName,
                                   java.time.LocalTime timeOfDay, String label) {

    public static PrescriptionResponse from(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getMedication().getId(),
                prescription.getMedication().getName(),
                prescription.getTimeOfDay(),
                prescription.getLabel()
        );
    }
}
