package com.project.back_end.services;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {
    private final AdminRepository admins; private final PatientRepository patients;
    private final TokenService tokens; private final PasswordEncoder passwords;
    public Service(AdminRepository admins, PatientRepository patients, TokenService tokens, PasswordEncoder passwords) {
        this.admins=admins; this.patients=patients; this.tokens=tokens; this.passwords=passwords;
    }
    public Map<String,Object> validateAdmin(Login login) {
        Admin a=admins.findByUsername(login.email()).orElseThrow(Service::badLogin);
        if(!passwords.matches(login.password(),a.getPassword())) throw badLogin();
        return Map.of("token",tokens.generateToken(a.getUsername(),"admin"),"role","admin","name",a.getUsername());
    }
    public Map<String,Object> validatePatientLogin(Login login) {
        Patient p=patients.findByEmail(login.email()).orElseThrow(Service::badLogin);
        if(!passwords.matches(login.password(),p.getPassword())) throw badLogin();
        return Map.of("token",tokens.generateToken(p.getEmail(),"patient"),"role","patient","name",p.getName());
    }
    public boolean validatePatient(Patient p) {return patients.findByEmailOrPhone(p.getEmail(),p.getPhone()).isEmpty();}
    public TokenService.Identity validateToken(String token,String role) {return tokens.require(token,role);}
    public static ApiException badLogin() {return new ApiException(HttpStatus.UNAUTHORIZED,"Email/username or password is incorrect");}
}
