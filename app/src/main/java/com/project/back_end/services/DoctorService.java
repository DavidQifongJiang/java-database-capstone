package com.project.back_end.services;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.time.*;

@org.springframework.stereotype.Service
public class DoctorService {
    private final DoctorRepository doctors; private final AppointmentRepository appointments;
    private final TokenService tokens; private final PasswordEncoder passwords;
    public DoctorService(DoctorRepository doctors,AppointmentRepository appointments,TokenService tokens,PasswordEncoder passwords) {
        this.doctors=doctors; this.appointments=appointments; this.tokens=tokens; this.passwords=passwords;
    }
    @Transactional(readOnly=true)
    public List<String> getDoctorAvailability(Long id,LocalDate date) {
        Doctor d=doctors.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Doctor not found"));
        if(date.isBefore(LocalDate.now()) || d.getUnavailableDates().contains(date)) return List.of();
        Set<LocalTime> occupied=new HashSet<>();
        appointments.findByDoctorIdAndAppointmentTimeGreaterThanEqualAndAppointmentTimeLessThanOrderByAppointmentTime(id,date.atStartOfDay(),date.plusDays(1).atStartOfDay())
            .forEach(a->occupied.add(a.getAppointmentTimeOnly()));
        return d.getAvailableTimes().stream().filter(slot->{LocalTime time=slotStart(slot); return !occupied.contains(time) && date.atTime(time).isAfter(LocalDateTime.now());}).sorted().toList();
    }
    public static LocalTime slotStart(String slot) {
        try {return LocalTime.parse(slot.split("-")[0].trim());}
        catch(RuntimeException e) {throw new ApiException(HttpStatus.BAD_REQUEST,"Slots must use HH:mm - HH:mm format");}
    }
    private void checkSlots(Doctor d) {
        if(d.getAvailableTimes()==null || d.getAvailableTimes().isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST,"At least one availability slot is required");
        Set<LocalTime> starts=new HashSet<>();
        for(String slot:d.getAvailableTimes()) {
            LocalTime start=slotStart(slot); String[] parts=slot.split("-");
            try {if(parts.length!=2 || !LocalTime.parse(parts[1].trim()).equals(start.plusHours(1)) || !starts.add(start)) throw new IllegalArgumentException();}
            catch(RuntimeException e) {throw new ApiException(HttpStatus.BAD_REQUEST,"Use distinct one-hour slots in HH:mm - HH:mm format");}
        }
    }
    @Transactional public Doctor saveDoctor(Doctor d) {
        checkSlots(d);
        if(doctors.findByEmail(d.getEmail()).isPresent()) throw new ApiException(HttpStatus.CONFLICT,"Doctor email already exists");
        d.setId(null);d.setPassword(passwords.encode(d.getPassword()));return doctors.save(d);
    }
    @Transactional public Doctor updateDoctor(Doctor d) {
        Doctor old=doctors.findById(d.getId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Doctor not found"));
        checkSlots(d);d.setPassword(passwords.encode(d.getPassword()));d.setUnavailableDates(old.getUnavailableDates());return doctors.save(d);
    }
    @Transactional(readOnly=true) public List<Doctor> getDoctors() {return doctors.findAll();}
    @Transactional public void deleteDoctor(Long id) {
        if(!doctors.existsById(id)) throw new ApiException(HttpStatus.NOT_FOUND,"Doctor not found");
        appointments.deleteByDoctorId(id);doctors.deleteById(id);
    }
    public Map<String,Object> validateDoctor(Login login) {
        Doctor d=doctors.findByEmail(login.email()).orElseThrow(Service::badLogin);
        if(!passwords.matches(login.password(),d.getPassword())) throw Service.badLogin();
        return Map.of("token",tokens.generateToken(d.getEmail(),"doctor"),"role","doctor","name",d.getName());
    }
    @Transactional(readOnly=true) public List<Doctor> filterDoctors(String name,String specialty,String time) {
        if(!time.isBlank() && !Set.of("AM","PM").contains(time.toUpperCase(Locale.ROOT))) throw new ApiException(HttpStatus.BAD_REQUEST,"Time must be AM or PM");
        return doctors.findAll().stream()
            .filter(d->d.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))
            .filter(d->d.getSpecialty().toLowerCase(Locale.ROOT).contains(specialty.toLowerCase(Locale.ROOT)))
            .filter(d->time.isBlank() || d.getAvailableTimes().stream().anyMatch(s->time.equalsIgnoreCase(slotStart(s).getHour()<12?"AM":"PM"))).toList();
    }
    @Transactional(readOnly=true) public Doctor getDoctorById(Long id) {return doctors.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Doctor not found"));}
    @Transactional public Doctor markUnavailable(Long doctorId, LocalDate date, boolean unavailable) {
        Doctor d=doctors.lockById(doctorId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Doctor not found"));
        if(unavailable) d.getUnavailableDates().add(date); else d.getUnavailableDates().remove(date);
        return doctors.save(d);
    }
}
