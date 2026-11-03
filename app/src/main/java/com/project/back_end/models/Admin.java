package com.project.back_end.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import java.time.*;

@Entity
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable=false, unique=true)
    private String username;
    @NotBlank @Size(min=6, max=100) @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;
    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }
    public String getUsername() { return username; }
    public void setUsername(String value) { this.username = value; }
    public String getPassword() { return password; }
    public void setPassword(String value) { this.password = value; }
}
