package com.project.back_end.services;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import java.util.List;
@org.springframework.stereotype.Service
public class PatientService {
    private final PatientRepository patients;private final AppointmentRepository appointments;private final PasswordEncoder passwords;
    public PatientService(PatientRepository patients,AppointmentRepository appointments,PasswordEncoder passwords) {this.patients=patients;this.appointments=appointments;this.passwords=passwords;}
    @Transactional public Patient createPatient(Patient p) {
        if(patients.findByEmailOrPhone(p.getEmail(),p.getPhone()).isPresent()) throw new ApiException(HttpStatus.CONFLICT,"Email or phone is already registered");
        p.setId(null);p.setPassword(passwords.encode(p.getPassword()));return patients.save(p);
    }
    @Transactional(readOnly=true) public Patient getPatient(Long id) {return patients.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Patient not found"));}
    @Transactional(readOnly=true) public List<Appointment> getPatientAppointments(Long id) {return appointments.findByPatientIdOrderByAppointmentTimeDesc(id);}
}
