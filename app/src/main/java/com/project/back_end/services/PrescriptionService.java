package com.project.back_end.services;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@org.springframework.stereotype.Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptions;private final AppointmentRepository appointments;
    public PrescriptionService(PrescriptionRepository prescriptions,AppointmentRepository appointments) {this.prescriptions=prescriptions;this.appointments=appointments;}
    public Prescription savePrescription(Prescription p,Long doctorId) {
        Appointment a=appointments.findById(p.getAppointmentId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Appointment not found"));
        if(!a.getDoctor().getId().equals(doctorId)) throw new ApiException(HttpStatus.FORBIDDEN,"Only the assigned doctor may prescribe");
        p.setId(null);p.setPatientName(a.getPatient().getName());return prescriptions.save(p);
    }
    public List<Prescription> getPrescriptions(Long appointmentId,TokenService.Identity identity) {
        Appointment a=appointments.findById(appointmentId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Appointment not found"));
        boolean allowed=identity.role().equals("doctor") ? a.getDoctor().getId().equals(identity.id()) : a.getPatient().getId().equals(identity.id());
        if(!allowed) throw new ApiException(HttpStatus.FORBIDDEN,"This prescription belongs to another appointment");
        return prescriptions.findByAppointmentId(appointmentId);
    }
}
