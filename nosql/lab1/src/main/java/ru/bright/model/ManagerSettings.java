package ru.bright.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ManagerSettings(String managerId, @NotBlank String language, @NotBlank String theme,
                              @Min(1) @Max(100) int pageSize) {}
