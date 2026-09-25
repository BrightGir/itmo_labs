package ru.bright.repository;

import java.util.UUID;

public final class EtcdKeys {
    public static final String EVENTS = "/library/events/";
    public static final String DRAFTS = "/library/drafts/";
    public static final String ORDERS = "/library/orders/";
    public static final String SETTINGS = "/library/settings/";
    public static final String CONFLICT = "/library/experiments/conflict";

    private static final String VIEWS = "/library/views/";

    private EtcdKeys() {}

    public static String event(UUID id) { return EVENTS + id; }
    public static String draft(UUID id) { return DRAFTS + id; }
    public static String order(UUID id) { return ORDERS + id; }
    public static String settings(String managerId) { return SETTINGS + managerId; }
    public static String views(UUID eventId) { return VIEWS + eventId; }
}
