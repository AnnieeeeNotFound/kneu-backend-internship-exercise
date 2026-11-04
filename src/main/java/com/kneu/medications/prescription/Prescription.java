// Prescription = one row of the "prescriptions" table: a medication, a time of day, an optional label.
// Calls: Medication (linked). Used by: PrescriptionService (creates), PrescriptionRepository (saves/loads), PrescriptionResponse (reads).
package com.kneu.medications.prescription;

import com.kneu.medications.Medication;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A prescription = which medication, what time of day to take it, optional label.
 */
@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    private LocalTime timeOfDay;

    private String label;

    protected Prescription() {
    }

    public Prescription(Medication medication, LocalTime timeOfDay, String label) {
        this.medication = medication;
        this.timeOfDay = timeOfDay;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public Medication getMedication() {
        return medication;
    }

    public LocalTime getTimeOfDay() {
        return timeOfDay;
    }

    public String getLabel() {
        return label;
    }
}
