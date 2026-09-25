package ru.bright.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.bright.TestObjects;
import ru.bright.model.Event;
import ru.bright.repository.InMemoryEtcdGateway;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class EventServiceTest {
    private static final Instant NOW = Instant.parse("2026-09-12T12:00:00Z");

    private EventService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new EventService(new InMemoryEtcdGateway(clock), TestObjects.jsonConverter(), clock);
    }

    @Test
    void createsAndListsEvents() {
        Event event = service.createEvent("Book presentation", "PRESENTATION", NOW.plusSeconds(3600), 50);

        assertThat(service.getEvent(event.id())).contains(event);
        assertThat(service.listEvents()).containsExactly(event);
    }

    @Test
    void incrementsViewsAtomically() {
        Event event = service.createEvent("Reading club", "MEETING", NOW, 20);

        java.util.stream.IntStream.range(0, 100).parallel().forEach(i -> service.registerView(event.id()));

        assertThat(service.getViews(event.id())).isEqualTo(100);
    }
}
