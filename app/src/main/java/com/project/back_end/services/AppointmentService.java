package com.project.back_end.services;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.time.*;

@org.springframework.stereotype.Service
public class AppointmentService {
    private final AppointmentRepository appointments;private final DoctorRepository doctors;
    private final PatientRepository patients;private final DoctorService doctorService;
    public AppointmentService(AppointmentRepository appointments,DoctorRepository doctors,PatientRepository patients,DoctorService doctorService) {
        this.appointments=appointments;this.doctors=doctors;this.patients=patients;this.doctorService=doctorService;
    }
    @Transactional public Appointment bookAppointment(Appointment requested,Long patientId) {
        Doctor d=doctors.lockById(requested.getDoctor().getId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Doctor not found"));
        Patient p=patients.findById(patientId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Patient not found"));
        validateSlot(d.getId(),requested.getAppointmentTime());
        Appointment a=new Appointment();a.setDoctor(d);a.setPatient(p);a.setAppointmentTime(requested.getAppointmentTime());a.setStatus(0);
        return appointments.saveAndFlush(a);
    }
    private void validateSlot(Long doctorId,LocalDateTime time) {
        if(time==null || !time.isAfter(LocalDateTime.now())) throw new ApiException(HttpStatus.BAD_REQUEST,"Appointment must be in the future");
        boolean available=doctorService.getDoctorAvailability(doctorId,time.toLocalDate()).stream().anyMatch(s->DoctorService.slotStart(s).equals(time.toLocalTime()));
        if(!available) throw new ApiException(HttpStatus.CONFLICT,"Appointment slot is unavailable");
    }
    @Transactional public Appointment updateAppointment(Long id,Appointment requested,Long patientId) {
        Appointment a=owned(id,patientId);Long doctorId=requested.getDoctor().getId();
        Doctor d=doctors.lockById(doctorId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Doctor not found"));
        if(!a.getDoctor().getId().equals(doctorId) || !a.getAppointmentTime().equals(requested.getAppointmentTime())) validateSlot(doctorId,requested.getAppointmentTime());
        a.setDoctor(d);a.setAppointmentTime(requested.getAppointmentTime());return appointments.saveAndFlush(a);
    }
    private Appointment owned(Long id,Long patientId) {
        Appointment a=appointments.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Appointment not found"));
        if(!a.getPatient().getId().equals(patientId)) throw new ApiException(HttpStatus.FORBIDDEN,"This appointment belongs to another patient");
        if(a.getStatus()!=0 || !a.getAppointmentTime().isAfter(LocalDateTime.now())) throw new ApiException(HttpStatus.CONFLICT,"Only upcoming appointments can be changed");
        return a;
    }
    @Transactional public void cancelAppointment(Long id,Long patientId) {appointments.delete(owned(id,patientId));}
    @Transactional(readOnly=true) public List<Appointment> getAppointments(Long doctorId,LocalDate date,String patientName) {
        return appointments.findByDoctorIdAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThanOrderByAppointmentTime(doctorId,date.atStartOfDay(),date.plusDays(1).atStartOfDay()).stream()
            .filter(a->a.getPatient().getName().toLowerCase(Locale.ROOT).contains(patientName.toLowerCase(Locale.ROOT))).toList();
    }
    @Transactional public Appointment changeStatus(Long id,Long doctorId) {
        Appointment a=appointments.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Appointment not found"));
        if(!a.getDoctor().getId().equals(doctorId)) throw new ApiException(HttpStatus.FORBIDDEN,"Appointment is assigned to another doctor");
        a.setStatus(1);return appointments.save(a);
    }
}
