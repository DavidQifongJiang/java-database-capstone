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
@RequestMapping("/patient")
public class PatientController {
    private final PatientService patients;private final com.project.back_end.services.Service service;private final TokenService tokens;
    public PatientController(PatientService patients,com.project.back_end.services.Service service,TokenService tokens) {this.patients=patients;this.service=service;this.tokens=tokens;}
    @PostMapping public ResponseEntity<Map<String,Object>> createPatient(@Valid @RequestBody Patient patient) {return ResponseEntity.status(201).body(Map.of("patient",patients.createPatient(patient),"message","Patient registered"));}
    @PostMapping("/login") public ResponseEntity<Map<String,Object>> login(@Valid @RequestBody Login login) {return AuthResponses.login(service.validatePatientLogin(login));}
    @GetMapping public ResponseEntity<Map<String,Object>> getPatient(@RequestHeader(value="Authorization",required=false) String token) {return ResponseEntity.ok(Map.of("patient",patients.getPatient(tokens.require(token,"patient").id())));}
    @GetMapping("/appointments") public ResponseEntity<Map<String,Object>> getPatientAppointment(@RequestHeader(value="Authorization",required=false) String token) {return ResponseEntity.ok(Map.of("appointments",patients.getPatientAppointments(tokens.require(token,"patient").id())));}
}
