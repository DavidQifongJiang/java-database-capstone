package com.project.back_end;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.*;
import com.project.back_end.repo.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CalendarContractTest {
    @Autowired DoctorRepository doctors;
    @Test void doctorNameQueryMatchesCaseInsensitiveSubstring() {
        Doctor d=new Doctor();d.setName("Avery Chen");d.setSpecialty("Cardiology");d.setEmail("query@example.test");d.setPassword("hash-placeholder");d.setPhone("2025550123");d.setAvailableTimes(List.of("09:00 - 10:00"));
        doctors.saveAndFlush(d);
        assertEquals(d.getId(),doctors.findByNameContainingIgnoreCase("ERY CH").get(0).getId());
        assertTrue(doctors.findByNameContainingIgnoreCase("Nonexistent").isEmpty());
    }
    @Test void calendarDtoContainsDisplayDataAndDerivesOneHourEnd() {
        Doctor d=new Doctor();d.setId(3L);d.setName("Avery Chen");
        Patient p=new Patient();p.setId(7L);p.setName("Jordan Blake");p.setEmail("patient@example.test");p.setPhone("2025550200");p.setAddress("Fictional address");
        Appointment a=new Appointment();a.setId(9L);a.setDoctor(d);a.setPatient(p);a.setAppointmentTime(LocalDateTime.of(2030,1,2,9,0));a.setStatus(0);
        AppointmentDTO dto=AppointmentDTO.from(a);
        assertEquals(3L,dto.getDoctorId());assertEquals(7L,dto.getPatientId());assertEquals("Jordan Blake",dto.getPatientName());
        assertEquals(LocalDate.of(2030,1,2),dto.getAppointmentDate());assertEquals(LocalTime.of(9,0),dto.getAppointmentTimeOnly());assertEquals(a.getAppointmentTime().plusHours(1),dto.getEndTime());
    }
}
