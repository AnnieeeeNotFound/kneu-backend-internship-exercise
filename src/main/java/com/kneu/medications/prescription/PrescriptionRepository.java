// Database access for prescriptions - Spring Data writes all the SQL for us.
// Calls: JpaRepository built-ins (save, findById...) + our two @Query join-fetch queries. Used by: PrescriptionService.
package com.kneu.medications.prescription;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // join fetch so the medication is loaded in the same query
    // (open-in-view is off, lazy loading would fail during JSON serialisation)
    @Query("select p from Prescription p join fetch p.medication")
    List<Prescription> findAllWithMedication();

    @Query("select p from Prescription p join fetch p.medication where p.id = :id")
    Optional<Prescription> findWithMedicationById(@Param("id") Long id);
}
