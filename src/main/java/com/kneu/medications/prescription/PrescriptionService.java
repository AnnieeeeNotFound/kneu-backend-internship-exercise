// The rule-keeper: validates the request, checks the medication exists, saves the prescription.
// Calls: PrescriptionRepository, MedicationRepository; throws NotFoundException / InvalidRequestException. Used by: PrescriptionController.
package com.kneu.medications.prescription;

import java.util.List;

import com.kneu.medications.Medication;
import com.kneu.medications.MedicationRepository;
import com.kneu.medications.error.InvalidRequestException;
import com.kneu.medications.error.NotFoundException;

import org.springframework.stereotype.Service;

/**
 * Validation and business rules for prescriptions.
 */
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptions;
    private final MedicationRepository medications;

    public PrescriptionService(PrescriptionRepository prescriptions, MedicationRepository medications) {
        this.prescriptions = prescriptions;
        this.medications = medications;
    }

    public Prescription create(CreatePrescriptionRequest request) {
        if (request.medicationId() == null) {
            throw new InvalidRequestException("medicationId is required");
        }
        if (request.timeOfDay() == null) {
            throw new InvalidRequestException("timeOfDay is required (e.g. \"08:00\")");
        }

        String label = normaliseLabel(request.label());

        Medication medication = medications.findById(request.medicationId())
                .orElseThrow(() -> new NotFoundException(
                        "Medication " + request.medicationId() + " does not exist"));

        return prescriptions.save(new Prescription(medication, request.timeOfDay(), label));
    }

    public List<Prescription> findAll() {
        return prescriptions.findAllWithMedication();
    }

    public Prescription findById(Long id) {
        return prescriptions.findWithMedicationById(id)
                .orElseThrow(() -> new NotFoundException("Prescription " + id + " does not exist"));
    }

    // blank label -> store null, cap it at 100 chars to keep it a short nickname
    private String normaliseLabel(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        String trimmed = label.trim();
        if (trimmed.length() > 100) {
            throw new InvalidRequestException("label must be at most 100 characters");
        }
        return trimmed;
    }
}
