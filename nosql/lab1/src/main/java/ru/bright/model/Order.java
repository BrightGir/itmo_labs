package ru.bright.model;

import java.time.Instant;
import java.util.UUID;

public record Order(UUID id, UUID draftId, UUID eventId, String managerId, int quantity,
                    String status, Instant createdAt) {}
