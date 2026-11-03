package com.project.back_end.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
@Document(collection="prescriptions")
public class Prescription {
    @Id private String id;
    @NotBlank @Size(min=3,max=100) private String patientName;
    @NotNull @Positive private Long appointmentId;
    @NotBlank @Size(min=3,max=100) private String medication;
    @NotBlank @Size(min=3,max=20) private String dosage;
    @Size(max=200) private String doctorNotes;
    public Prescription() {}
    public Prescription(String patientName, Long appointmentId, String medication, String dosage) {
        this.patientName=patientName; this.appointmentId=appointmentId; this.medication=medication; this.dosage=dosage;
    }
    public String getId() {return id;}
    public void setId(String value) {this.id=value;}
    public String getPatientName() {return patientName;}
    public void setPatientName(String value) {this.patientName=value;}
    public Long getAppointmentId() {return appointmentId;}
    public void setAppointmentId(Long value) {this.appointmentId=value;}
    public String getMedication() {return medication;}
    public void setMedication(String value) {this.medication=value;}
    public String getDosage() {return dosage;}
    public void setDosage(String value) {this.dosage=value;}
    public String getDoctorNotes() {return doctorNotes;}
    public void setDoctorNotes(String value) {this.doctorNotes=value;}
}
