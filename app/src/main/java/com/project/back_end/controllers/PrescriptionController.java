package com.project.back_end.controllers;
import com.project.back_end.models.*;
import com.project.back_end.services.*;
import com.project.back_end.DTO.Login;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.*;
import java.time.*;
@RestController
@RequestMapping("/prescription")
public class PrescriptionController {
    private final PrescriptionService prescriptions;private final TokenService tokens;
    public PrescriptionController(PrescriptionService prescriptions,TokenService tokens) {this.prescriptions=prescriptions;this.tokens=tokens;}
    @PostMapping public ResponseEntity<Map<String,Object>> savePrescription(@Valid @RequestBody Prescription prescription,@RequestHeader(value="Authorization",required=false) String token) {
        Long doctorId=tokens.require(token,"doctor").id();
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("prescription",prescriptions.savePrescription(prescription,doctorId),"message","Prescription saved"));
    }
    @GetMapping("/{appointmentId}") public ResponseEntity<Map<String,Object>> getPrescription(@PathVariable Long appointmentId,@RequestParam String role,@RequestHeader(value="Authorization",required=false) String token) {
        if(!Set.of("doctor","patient").contains(role)) throw new ApiException(HttpStatus.FORBIDDEN,"Doctor or patient role required");
        return ResponseEntity.ok(Map.of("prescriptions",prescriptions.getPrescriptions(appointmentId,tokens.require(token,role))));
    }
}
