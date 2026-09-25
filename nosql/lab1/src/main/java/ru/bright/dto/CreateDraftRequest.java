package ru.bright.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDraftRequest(@NotNull UUID eventId,
                                 @NotBlank String managerId,
                                 @Min(1) int quantity,
                                 String comment,
                                 @Min(5) @Max(86_400) long ttlSeconds) {}
