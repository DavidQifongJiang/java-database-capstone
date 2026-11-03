package com.project.back_end.controllers;
import com.project.back_end.models.*;
import com.project.back_end.services.*;
import com.project.back_end.DTO.Login;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.*;
import java.time.*;
public final class AuthResponses {
    private AuthResponses() {}
    public static ResponseEntity<Map<String,Object>> login(Map<String,Object> result) {
        ResponseCookie cookie=ResponseCookie.from("clinic_session",(String)result.get("token"))
            .httpOnly(true).sameSite("Strict").path("/").maxAge(Duration.ofDays(7)).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(result);
    }
}
