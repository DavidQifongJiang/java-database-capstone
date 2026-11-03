package com.project.back_end;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import com.project.back_end.services.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;
import java.time.*;

class BackEndApplicationTests {
    private TokenService tokens(PatientRepository patients) {
        return new TokenService(Base64.getEncoder().encodeToString(new byte[32]),mock(AdminRepository.class),mock(DoctorRepository.class),patients);
    }
    @Test void signedTokenRetainsSubjectAndRejectsWrongRoleAndTampering() {
        PatientRepository repo=mock(PatientRepository.class);Patient p=new Patient();p.setId(5L);when(repo.findByEmail("test@example.test")).thenReturn(Optional.of(p));
        TokenService tokens=tokens(repo);String token=tokens.generateToken("test@example.test","patient");
        assertEquals("test@example.test",tokens.extractEmail(token));assertTrue(tokens.validateToken(token,"patient"));assertFalse(tokens.validateToken(token,"doctor"));
        assertFalse(tokens.validateToken(token.substring(0,token.lastIndexOf('.')+1)+"invalidsignature","patient"));
    }
    @Test void deletedAccountTokenIsRejected() {
        TokenService tokens=tokens(mock(PatientRepository.class));
        assertFalse(tokens.validateToken(tokens.generateToken("missing@example.test","patient"),"patient"));
    }
    @Test void availabilityExcludesBookedAndUnavailableDates() {
        DoctorRepository dr=mock(DoctorRepository.class);AppointmentRepository ar=mock(AppointmentRepository.class);
        Doctor d=new Doctor();d.setId(1L);d.setAvailableTimes(List.of("09:00 - 10:00","10:00 - 11:00"));when(dr.findById(1L)).thenReturn(Optional.of(d));
        LocalDate day=LocalDate.now().plusDays(3);Appointment booked=new Appointment();booked.setAppointmentTime(day.atTime(9,0));
        when(ar.findByDoctorIdAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThanOrderByAppointmentTime(1L,day.atStartOfDay(),day.plusDays(1).atStartOfDay())).thenReturn(List.of(booked));
        DoctorService s=new DoctorService(dr,ar,mock(TokenService.class),new BCryptPasswordEncoder());
        assertEquals(List.of("10:00 - 11:00"),s.getDoctorAvailability(1L,day));d.getUnavailableDates().add(day);assertEquals(List.of(),s.getDoctorAvailability(1L,day));
    }
    @Test void patientCannotCancelAnotherPatientsAppointment() {
        AppointmentRepository ar=mock(AppointmentRepository.class);Patient owner=new Patient();owner.setId(1L);Appointment a=new Appointment();a.setPatient(owner);a.setStatus(0);a.setAppointmentTime(LocalDateTime.now().plusDays(1));when(ar.findById(8L)).thenReturn(Optional.of(a));
        AppointmentService s=new AppointmentService(ar,mock(DoctorRepository.class),mock(PatientRepository.class),mock(DoctorService.class));
        ApiException e=assertThrows(ApiException.class,()->s.cancelAppointment(8L,2L));assertEquals(403,e.getStatus().value());verify(ar,never()).delete(any());
    }
    @Test void bookingUsesAuthenticatedPatientAndResetsClientSuppliedStatus() {
        AppointmentRepository ar=mock(AppointmentRepository.class);DoctorRepository dr=mock(DoctorRepository.class);PatientRepository pr=mock(PatientRepository.class);DoctorService ds=mock(DoctorService.class);
        Doctor d=new Doctor();d.setId(1L);Patient real=new Patient();real.setId(7L);Patient spoofed=new Patient();spoofed.setId(99L);
        LocalDate day=LocalDate.now().plusDays(2);Appointment request=new Appointment();request.setDoctor(d);request.setPatient(spoofed);request.setAppointmentTime(day.atTime(9,0));request.setStatus(1);
        when(dr.lockById(1L)).thenReturn(Optional.of(d));when(pr.findById(7L)).thenReturn(Optional.of(real));when(ds.getDoctorAvailability(1L,day)).thenReturn(List.of("09:00 - 10:00"));when(ar.saveAndFlush(any())).thenAnswer(i->i.getArgument(0));
        Appointment result=new AppointmentService(ar,dr,pr,ds).bookAppointment(request,7L);assertEquals(7L,result.getPatient().getId());assertEquals(0,result.getStatus());
    }
    @Test void unavailableSlotIsRejectedBeforeSave() {
        AppointmentRepository ar=mock(AppointmentRepository.class);DoctorRepository dr=mock(DoctorRepository.class);PatientRepository pr=mock(PatientRepository.class);DoctorService ds=mock(DoctorService.class);
        Doctor d=new Doctor();d.setId(1L);Patient p=new Patient();p.setId(1L);LocalDate day=LocalDate.now().plusDays(2);Appointment a=new Appointment();a.setDoctor(d);a.setAppointmentTime(day.atTime(9,0));
        when(dr.lockById(1L)).thenReturn(Optional.of(d));when(pr.findById(1L)).thenReturn(Optional.of(p));when(ds.getDoctorAvailability(1L,day)).thenReturn(List.of());
        assertThrows(ApiException.class,()->new AppointmentService(ar,dr,pr,ds).bookAppointment(a,1L));verify(ar,never()).saveAndFlush(any());
    }
}
