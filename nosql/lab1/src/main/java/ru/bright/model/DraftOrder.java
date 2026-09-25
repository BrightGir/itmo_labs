package ru.bright.model;

import java.time.Instant;
import java.util.UUID;

public record DraftOrder(UUID id, UUID eventId, String managerId, int quantity, String comment, Instant createdAt) {}
