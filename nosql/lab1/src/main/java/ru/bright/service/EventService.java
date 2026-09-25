package ru.bright.service;

import org.springframework.stereotype.Service;
import ru.bright.exception.ResourceConflictException;
import ru.bright.exception.ResourceNotFoundException;
import ru.bright.model.Event;
import ru.bright.repository.EtcdGateway;
import ru.bright.repository.EtcdKeys;
import ru.bright.repository.JsonConverter;
import ru.bright.repository.StoredValue;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private static final int MAX_COUNTER_ATTEMPTS = 1_000;

    private final EtcdGateway gateway;
    private final JsonConverter json;
    private final Clock clock;

    public EventService(EtcdGateway gateway, JsonConverter json, Clock clock) {
        this.gateway = gateway;
        this.json = json;
        this.clock = clock;
    }

    public Event createEvent(String title, String type, Instant startsAt, int capacity) {
        Event event = new Event(UUID.randomUUID(), title, type, startsAt, capacity, clock.instant());
        gateway.put(EtcdKeys.event(event.id()), json.write(event));
        return event;
    }

    public Optional<Event> getEvent(UUID id) {
        return gateway.get(EtcdKeys.event(id)).map(value -> json.read(value.value(), Event.class));
    }

    public List<Event> listEvents() {
        return gateway.getPrefix(EtcdKeys.EVENTS).stream()
                .map(value -> json.read(value.value(), Event.class))
                .toList();
    }

    public long registerView(UUID eventId) {
        if (getEvent(eventId).isEmpty()) {
            throw new ResourceNotFoundException("Event not found: " + eventId);
        }

        String key = EtcdKeys.views(eventId);
        for (int attempt = 0; attempt < MAX_COUNTER_ATTEMPTS; attempt++) {
            Optional<StoredValue> stored = gateway.get(key);
            long oldValue = stored.map(value -> Long.parseLong(value.value())).orElse(0L);
            long revision = stored.map(StoredValue::modificationRevision).orElse(0L);
            if (gateway.compareAndSet(key, revision, Long.toString(oldValue + 1))) {
                return oldValue + 1;
            }
        }
        throw new ResourceConflictException("Counter is too busy; retry the request");
    }

    public long getViews(UUID eventId) {
        return gateway.get(EtcdKeys.views(eventId))
                .map(value -> Long.parseLong(value.value()))
                .orElse(0L);
    }
}
