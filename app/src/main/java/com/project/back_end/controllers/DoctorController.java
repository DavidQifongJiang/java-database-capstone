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
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorService doctors; private final TokenService tokens;
    public DoctorController(DoctorService doctors,TokenService tokens) {this.doctors=doctors;this.tokens=tokens;}
    @GetMapping public ResponseEntity<Map<String,Object>> getDoctor() {return ResponseEntity.ok(Map.of("doctors",doctors.getDoctors()));}
    @GetMapping("/filter") public ResponseEntity<Map<String,Object>> filter(@RequestParam(defaultValue="") String name,@RequestParam(defaultValue="") String specialty,@RequestParam(defaultValue="") String time) {
        return ResponseEntity.ok(Map.of("doctors",doctors.filterDoctors(name,specialty,time)));
    }
    @GetMapping("/availability/{user}/{doctorId}/{date}")
    public ResponseEntity<Map<String,Object>> getDoctorAvailability(@PathVariable String user,@PathVariable Long doctorId,@PathVariable LocalDate date,@RequestHeader(value="Authorization",required=false) String token) {
        tokens.require(token,user);return ResponseEntity.ok(Map.of("doctorId",doctorId,"date",date,"availableTimes",doctors.getDoctorAvailability(doctorId,date)));
    }
    @PostMapping public ResponseEntity<Map<String,Object>> saveDoctor(@Valid @RequestBody Doctor doctor,@RequestHeader(value="Authorization",required=false) String token) {
        tokens.require(token,"admin");return ResponseEntity.status(201).body(Map.of("doctor",doctors.saveDoctor(doctor),"message","Doctor added"));
    }
    @PostMapping("/login") public ResponseEntity<Map<String,Object>> doctorLogin(@Valid @RequestBody Login login) {return AuthResponses.login(doctors.validateDoctor(login));}
    @PutMapping public ResponseEntity<Map<String,Object>> updateDoctor(@Valid @RequestBody Doctor doctor,@RequestHeader(value="Authorization",required=false) String token) {
        tokens.require(token,"admin");return ResponseEntity.ok(Map.of("doctor",doctors.updateDoctor(doctor)));
    }
    @GetMapping("/profile") public ResponseEntity<Map<String,Object>> getProfile(@RequestHeader(value="Authorization",required=false) String token) {return ResponseEntity.ok(Map.of("doctor",doctors.getDoctorById(tokens.require(token,"doctor").id())));}
    @PutMapping("/profile") public ResponseEntity<Map<String,Object>> updateProfile(@Valid @RequestBody Doctor doctor,@RequestHeader(value="Authorization",required=false) String token) {
        doctor.setId(tokens.require(token,"doctor").id());return ResponseEntity.ok(Map.of("doctor",doctors.updateDoctor(doctor)));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Map<String,Object>> deleteDoctor(@PathVariable Long id,@RequestHeader(value="Authorization",required=false) String token) {
        tokens.require(token,"admin");doctors.deleteDoctor(id);return ResponseEntity.ok(Map.of("message","Doctor removed"));
    }
    @PutMapping("/unavailable/{date}") public ResponseEntity<Map<String,Object>> markUnavailable(@PathVariable LocalDate date,@RequestParam(defaultValue="true") boolean unavailable,@RequestHeader(value="Authorization",required=false) String token) {
        Long id=tokens.require(token,"doctor").id();return ResponseEntity.ok(Map.of("doctor",doctors.markUnavailable(id,date,unavailable)));
    }
}
