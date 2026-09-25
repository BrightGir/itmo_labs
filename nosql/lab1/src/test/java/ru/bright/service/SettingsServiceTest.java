package ru.bright.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.bright.TestObjects;
import ru.bright.model.ManagerSettings;
import ru.bright.repository.InMemoryEtcdGateway;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class SettingsServiceTest {
    private InMemoryEtcdGateway gateway;
    private SettingsService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-12T12:00:00Z"), ZoneOffset.UTC);
        gateway = new InMemoryEtcdGateway(clock);
        service = new SettingsService(gateway, TestObjects.jsonConverter(), clock, 60);
    }

    @Test
    void cachesSettingsAndInvalidatesCacheOnUpdate() {
        ManagerSettings initial = new ManagerSettings("manager-1", "ru", "dark", 20);
        service.saveSettings(initial);

        assertThat(service.getSettings("manager-1")).contains(initial);
        gateway.overwriteSettings("manager-1", new ManagerSettings("manager-1", "en", "light", 10));
        assertThat(service.getSettings("manager-1")).contains(initial);

        ManagerSettings updated = new ManagerSettings("manager-1", "en", "light", 50);
        service.saveSettings(updated);
        assertThat(service.getSettings("manager-1")).contains(updated);
    }
}
