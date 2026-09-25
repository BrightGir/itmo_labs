package ru.bright.dto;

import java.util.UUID;

public record ViewResponse(UUID eventId, long views) {}
