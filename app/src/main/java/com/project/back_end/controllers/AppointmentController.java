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
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointments;private final TokenService tokens;
    public AppointmentController(AppointmentService appointments,TokenService tokens) {this.appointments=appointments;this.tokens=tokens;}
    @GetMapping public ResponseEntity<Map<String,Object>> getAppointments(@RequestParam LocalDate date,@RequestParam(defaultValue="") String patientName,@RequestHeader(value="Authorization",required=false) String token) {
        return ResponseEntity.ok(Map.of("appointments",appointments.getAppointments(tokens.require(token,"doctor").id(),date,patientName)));
    }
    @PostMapping public ResponseEntity<Map<String,Object>> bookAppointment(@Valid @RequestBody Appointment appointment,@RequestHeader(value="Authorization",required=false) String token) {
        return ResponseEntity.status(201).body(Map.of("appointment",appointments.bookAppointment(appointment,tokens.require(token,"patient").id()),"message","Appointment booked"));
    }
    @PutMapping("/{id}") public ResponseEntity<Map<String,Object>> updateAppointment(@PathVariable Long id,@Valid @RequestBody Appointment appointment,@RequestHeader(value="Authorization",required=false) String token) {
        return ResponseEntity.ok(Map.of("appointment",appointments.updateAppointment(id,appointment,tokens.require(token,"patient").id())));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Map<String,Object>> cancelAppointment(@PathVariable Long id,@RequestHeader(value="Authorization",required=false) String token) {
        appointments.cancelAppointment(id,tokens.require(token,"patient").id());return ResponseEntity.ok(Map.of("message","Appointment cancelled"));
    }
    @PutMapping("/{id}/complete") public ResponseEntity<Map<String,Object>> complete(@PathVariable Long id,@RequestHeader(value="Authorization",required=false) String token) {
        return ResponseEntity.ok(Map.of("appointment",appointments.changeStatus(id,tokens.require(token,"doctor").id())));
    }
}
