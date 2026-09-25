package ru.bright.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.bright.TestObjects;
import ru.bright.exception.ResourceConflictException;
import ru.bright.model.DraftOrder;
import ru.bright.model.Event;
import ru.bright.model.Order;
import ru.bright.repository.InMemoryEtcdGateway;
import ru.bright.repository.JsonConverter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {
    private static final Instant NOW = Instant.parse("2026-09-12T12:00:00Z");

    private InMemoryEtcdGateway gateway;
    private EventService eventService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        gateway = new InMemoryEtcdGateway(clock);
        JsonConverter json = TestObjects.jsonConverter();
        eventService = new EventService(gateway, json, clock);
        orderService = new OrderService(gateway, json, clock,
                new SettingsService(gateway, json, clock, 60));
    }

    @Test
    void temporaryDraftExpiresAfterTtl() {
        Event event = eventService.createEvent("Book presentation", "PRESENTATION", NOW, 50);
        DraftOrder draft = orderService.createDraft(event.id(), "manager-1", 2, "front row", 30);

        assertThat(orderService.getDraft(draft.id())).contains(draft);
        gateway.advanceSeconds(31);
        assertThat(orderService.getDraft(draft.id())).isEmpty();
    }

    @Test
    void submittingDraftCreatesOrderAndConsumesDraftAtomically() {
        Event event = eventService.createEvent("Rare books", "EXHIBITION", NOW, 100);
        DraftOrder draft = orderService.createDraft(event.id(), "manager-1", 3, "student group", 60);

        Order order = orderService.submitDraft(draft.id());

        assertThat(order.eventId()).isEqualTo(event.id());
        assertThat(order.status()).isEqualTo("CREATED");
        assertThat(orderService.getDraft(draft.id())).isEmpty();
        assertThat(orderService.getOrder(order.id())).contains(order);
        assertThatThrownBy(() -> orderService.submitDraft(draft.id()))
                .isInstanceOf(ResourceConflictException.class);
    }
}
