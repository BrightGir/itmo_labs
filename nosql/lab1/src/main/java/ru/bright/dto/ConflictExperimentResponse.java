package ru.bright.dto;

public record ConflictExperimentResponse(int writers, long blindWriteFinalValue,
                                         long casFinalValue, long casConflicts) {}
