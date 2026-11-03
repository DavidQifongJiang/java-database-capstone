package com.project.back_end.repo;
import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
import java.time.LocalDateTime;
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    List<Appointment> findByDoctorIdAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThanOrderByAppointmentTime(Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdOrderByAppointmentTimeDesc(Long patientId);
    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);
    void deleteByDoctorId(Long doctorId);
}
