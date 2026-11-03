package com.project.back_end.DTO;
import java.time.*;
import com.project.back_end.models.Appointment;

/** Stable calendar representation without persistence entities or password fields. */
public class AppointmentDTO {
    private final Long id;
    private final Long doctorId;
    private final String doctorName;
    private final Long patientId;
    private final String patientName;
    private final String patientEmail;
    private final String patientPhone;
    private final String patientAddress;
    private final LocalDateTime appointmentTime;
    private final int status;
    public AppointmentDTO(Long id,Long doctorId,String doctorName,Long patientId,String patientName,String patientEmail,String patientPhone,String patientAddress,LocalDateTime appointmentTime,int status) {
        this.id=id;
        this.doctorId=doctorId;
        this.doctorName=doctorName;
        this.patientId=patientId;
        this.patientName=patientName;
        this.patientEmail=patientEmail;
        this.patientPhone=patientPhone;
        this.patientAddress=patientAddress;
        this.appointmentTime=appointmentTime;
        this.status=status;
    }
    public static AppointmentDTO from(Appointment a) { return new AppointmentDTO(a.getId(),a.getDoctor().getId(),a.getDoctor().getName(),a.getPatient().getId(),a.getPatient().getName(),a.getPatient().getEmail(),a.getPatient().getPhone(),a.getPatient().getAddress(),a.getAppointmentTime(),a.getStatus()); }
    public Long getId() { return id; }
    public Long getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public Long getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getPatientEmail() { return patientEmail; }
    public String getPatientPhone() { return patientPhone; }
    public String getPatientAddress() { return patientAddress; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public int getStatus() { return status; }
    public LocalDate getAppointmentDate(){return appointmentTime.toLocalDate();}
    public LocalTime getAppointmentTimeOnly(){return appointmentTime.toLocalTime();}
    public LocalDateTime getEndTime(){return appointmentTime.plusHours(1);}
}
