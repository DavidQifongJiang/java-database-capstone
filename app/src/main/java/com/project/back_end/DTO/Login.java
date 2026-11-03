package com.project.back_end.DTO;
import jakarta.validation.constraints.NotBlank;
public record Login(@NotBlank String email, @NotBlank String password) {}
