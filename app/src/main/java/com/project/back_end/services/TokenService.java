package com.project.back_end.services;
import com.project.back_end.repo.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.Date;

@Component
public class TokenService {
    private final SecretKey signingKey;
    private final AdminRepository admins;
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    public record Identity(String subject, String role, Long id) {}
    public TokenService(@Value("${jwt.secret}") String secret, AdminRepository admins, DoctorRepository doctors, PatientRepository patients) {
        this.signingKey=Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.admins=admins; this.doctors=doctors; this.patients=patients;
    }
    public SecretKey getSigningKey() {return signingKey;}
    public String generateToken(String email, String role) {
        Instant now=Instant.now();
        return Jwts.builder().subject(email).claim("role",role).issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(7*24*60*60))).signWith(getSigningKey()).compact();
    }
    private Claims claims(String token) {
        if(token==null || token.isBlank()) throw new ApiException(HttpStatus.UNAUTHORIZED,"Sign in is required");
        if(token.startsWith("Bearer ")) token=token.substring(7);
        try {return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();}
        catch(JwtException | IllegalArgumentException e) {throw new ApiException(HttpStatus.UNAUTHORIZED,"Invalid or expired token");}
    }
    public String extractEmail(String token) {return claims(token).getSubject();}
    public Identity require(String token, String expectedRole) {
        Claims c=claims(token); String role=c.get("role",String.class); String subject=c.getSubject();
        if(!expectedRole.equals(role)) throw new ApiException(HttpStatus.FORBIDDEN,"This role cannot access that resource");
        Long id=switch(role) {
            case "admin" -> admins.findByUsername(subject).map(a->a.getId()).orElse(null);
            case "doctor" -> doctors.findByEmail(subject).map(d->d.getId()).orElse(null);
            case "patient" -> patients.findByEmail(subject).map(p->p.getId()).orElse(null);
            default -> null;
        };
        if(id==null) throw new ApiException(HttpStatus.UNAUTHORIZED,"Account no longer exists");
        return new Identity(subject,role,id);
    }
    public boolean validateToken(String token, String role) {
        try {require(token,role); return true;} catch(ApiException e) {return false;}
    }
}
