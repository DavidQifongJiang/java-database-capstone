package com.project.back_end;
import com.project.back_end.models.*;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
class ModelValidationTest {
    @Test void invalidPatientFieldsAreRejected() {
        try(var factory=Validation.buildDefaultValidatorFactory()) {Patient p=new Patient();p.setName("X");p.setEmail("invalid");p.setPhone("123");p.setPassword("x");p.setAddress("");assertTrue(factory.getValidator().validate(p).size()>=5);}
    }
    @Test void passwordIsWriteOnlyInJson() throws Exception {
        ObjectMapper mapper=new ObjectMapper();Patient p=new Patient();p.setName("Example Person");p.setPassword("DemoPassword1!");assertFalse(mapper.writeValueAsString(p).contains("password"));assertEquals("DemoPassword1!",mapper.readValue("{\"password\":\"DemoPassword1!\"}",Patient.class).getPassword());
    }
    @Test void appointmentRejectsPastTime() {
        try(var factory=Validation.buildDefaultValidatorFactory()) {Appointment a=new Appointment();a.setDoctor(new Doctor());a.setPatient(new Patient());a.setAppointmentTime(LocalDateTime.now().minusDays(1));assertTrue(factory.getValidator().validate(a).stream().anyMatch(v->v.getPropertyPath().toString().equals("appointmentTime")));}
    }
}
