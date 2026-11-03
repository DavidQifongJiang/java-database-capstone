package com.project.back_end.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import java.time.*;

@Entity
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(min=3, max=100)
    private String name;
    @NotBlank @Size(min=3, max=50)
    private String specialty;
    @NotBlank @Email @Column(nullable=false, unique=true)
    private String email;
    @NotBlank @Size(min=6, max=100) @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank @Pattern(regexp="\\d{10}")
    private String phone;
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="doctor_available_times", joinColumns=@JoinColumn(name="doctor_id")) @Column(name="available_times")
    private List<String> availableTimes = new ArrayList<>();
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="doctor_unavailable_dates", joinColumns=@JoinColumn(name="doctor_id")) @Column(name="unavailable_date")
    private Set<LocalDate> unavailableDates = new HashSet<>();
    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }
    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String value) { this.specialty = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getPassword() { return password; }
    public void setPassword(String value) { this.password = value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { this.phone = value; }
    public List<String> getAvailableTimes() { return availableTimes; }
    public void setAvailableTimes(List<String> value) { this.availableTimes = value; }
    public Set<LocalDate> getUnavailableDates() { return unavailableDates; }
    public void setUnavailableDates(Set<LocalDate> value) { this.unavailableDates = value; }
}
