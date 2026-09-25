package ru.bright.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateEventRequest(@NotBlank String title,
                                 @NotBlank String type,
                                 @NotNull Instant startsAt,
                                 @Min(1) int capacity) {}
