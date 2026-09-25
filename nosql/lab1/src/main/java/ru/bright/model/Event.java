package ru.bright.model;

import java.time.Instant;
import java.util.UUID;

public record Event(UUID id, String title, String type, Instant startsAt, int capacity, Instant createdAt) {}
