package com.project.back_end;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest(properties={"spring.datasource.password=","spring.jpa.hibernate.ddl-auto=create-drop"})
class PatientRepositoryTest {
    @Autowired PatientRepository patients;
    @Test void searchesByEmailOrPhone() {
        Patient p=new Patient();p.setName("Test Person");p.setEmail("person@example.test");p.setPassword("DemoPassword1!");p.setPhone("2025550199");p.setAddress("Example address");patients.saveAndFlush(p);
        assertTrue(patients.findByEmail("person@example.test").isPresent());
        assertTrue(patients.findByEmailOrPhone("different@example.test","2025550199").isPresent());
        assertTrue(patients.findByEmailOrPhone("different@example.test","2025550188").isEmpty());
    }
}
