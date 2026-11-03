package com.project.back_end.config;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
@Component
@ConditionalOnProperty(name="clinic.seed",havingValue="true")
public class DemoData implements CommandLineRunner {
    private final AdminRepository admins;private final DoctorRepository doctors;private final PatientRepository patients;private final AppointmentRepository appointments;private final PasswordEncoder passwords;private final String demoPassword;
    public DemoData(AdminRepository admins,DoctorRepository doctors,PatientRepository patients,AppointmentRepository appointments,PasswordEncoder passwords,@Value("${clinic.demo-password}") String demoPassword) {
        this.admins=admins;this.doctors=doctors;this.patients=patients;this.appointments=appointments;this.passwords=passwords;this.demoPassword=demoPassword;
    }
    @Override @Transactional public void run(String... args) {
        if(admins.count()!=0 || doctors.count()!=0 || patients.count()!=0) return;
        String hash=passwords.encode(demoPassword);
        Admin admin=new Admin();admin.setUsername("admin");admin.setPassword(hash);admins.save(admin);
        String[][] doctorData={{"Avery Chen","Cardiology","avery.chen@example.test"},{"Morgan Lee","Dermatology","morgan.lee@example.test"},{"Taylor Rivera","General Medicine","taylor.rivera@example.test"}};
        List<Doctor> ds=new ArrayList<>();
        for(int i=0;i<doctorData.length;i++) {Doctor d=new Doctor();d.setName(doctorData[i][0]);d.setSpecialty(doctorData[i][1]);d.setEmail(doctorData[i][2]);d.setPassword(hash);d.setPhone("20255501"+String.format("%02d",i));d.setAvailableTimes(new ArrayList<>(List.of("09:00 - 10:00","10:00 - 11:00","11:00 - 12:00","14:00 - 15:00","15:00 - 16:00")));ds.add(doctors.save(d));}
        String[] names={"Jordan Blake","Casey Ellis","Riley Quinn","Alex Parker","Sam Avery"};
        for(int i=0;i<names.length;i++) {Patient p=new Patient();p.setName(names[i]);p.setEmail("patient"+(i+1)+"@example.test");p.setPassword(hash);p.setPhone("20255502"+String.format("%02d",i));p.setAddress((100+i)+" Example Lane, Demo City");patients.save(p);
            Appointment a=new Appointment();a.setPatient(p);a.setDoctor(ds.get(i<3?0:1));a.setAppointmentTime(LocalDate.now().plusDays(1).atTime(9+(i<3?i:i-3),0));a.setStatus(0);appointments.save(a);}
    }
}
