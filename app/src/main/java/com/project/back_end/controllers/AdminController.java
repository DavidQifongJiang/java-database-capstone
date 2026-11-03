package com.project.back_end.controllers;
import com.project.back_end.models.*;
import com.project.back_end.services.*;
import com.project.back_end.DTO.Login;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.*;
import java.time.*;
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final com.project.back_end.services.Service service;
    public AdminController(com.project.back_end.services.Service service) {this.service=service;}
    @PostMapping("/login") public ResponseEntity<Map<String,Object>> login(@Valid @RequestBody Login login) {return AuthResponses.login(service.validateAdmin(login));}
    @PostMapping("/logout") public ResponseEntity<Map<String,Object>> logout() {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,ResponseCookie.from("clinic_session","").httpOnly(true).sameSite("Strict").path("/").maxAge(0).build().toString()).body(Map.of("message","Signed out"));
    }
}
