package ru.bright.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ManagerSettings(String managerId, @NotNull @Min(1) Integer defaultQuantity) {}
