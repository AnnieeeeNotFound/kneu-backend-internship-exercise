// The front door: POST /prescriptions (create), GET /prescriptions (list), GET /prescriptions/{id} (single).
// Calls: PrescriptionService, PrescriptionResponse. Talked to by: clients over HTTP.
package com.kneu.medications.prescription;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService service;

    public PrescriptionController(PrescriptionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponse> create(@RequestBody CreatePrescriptionRequest request) {
        Prescription saved = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(PrescriptionResponse.from(saved));
    }

    @GetMapping
    public List<PrescriptionResponse> getAll() {
        return service.findAll().stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PrescriptionResponse getById(@PathVariable Long id) {
        return PrescriptionResponse.from(service.findById(id));
    }
}
