package ru.bright.service;

import org.springframework.stereotype.Service;
import ru.bright.exception.ResourceConflictException;
import ru.bright.exception.ResourceNotFoundException;
import ru.bright.model.DraftOrder;
import ru.bright.model.Order;
import ru.bright.repository.EtcdGateway;
import ru.bright.repository.EtcdKeys;
import ru.bright.repository.JsonConverter;
import ru.bright.repository.StoredValue;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final EtcdGateway gateway;
    private final JsonConverter json;
    private final Clock clock;

    public OrderService(EtcdGateway gateway, JsonConverter json, Clock clock) {
        this.gateway = gateway;
        this.json = json;
        this.clock = clock;
    }

    public DraftOrder createDraft(UUID eventId, String managerId, int quantity, String comment, long ttlSeconds) {
        if (gateway.get(EtcdKeys.event(eventId)).isEmpty()) {
            throw new ResourceNotFoundException("Event not found: " + eventId);
        }
        if (ttlSeconds < 5 || ttlSeconds > 86_400) {
            throw new IllegalArgumentException("TTL must be 5..86400 seconds");
        }

        DraftOrder draft = new DraftOrder(
                UUID.randomUUID(), eventId, managerId, quantity, comment, clock.instant());
        gateway.putWithTtl(EtcdKeys.draft(draft.id()), json.write(draft), ttlSeconds);
        return draft;
    }

    public Optional<DraftOrder> getDraft(UUID id) {
        return gateway.get(EtcdKeys.draft(id)).map(value -> json.read(value.value(), DraftOrder.class));
    }

    public Order submitDraft(UUID draftId) {
        StoredValue storedDraft = gateway.get(EtcdKeys.draft(draftId))
                .orElseThrow(() -> new ResourceConflictException(
                        "Draft is missing, expired, or already submitted"));
        DraftOrder draft = json.read(storedDraft.value(), DraftOrder.class);
        Order order = new Order(UUID.randomUUID(), draft.id(), draft.eventId(), draft.managerId(),
                draft.quantity(), "CREATED", clock.instant());

        boolean committed = gateway.createOrderAndDeleteDraft(
                EtcdKeys.event(draft.eventId()),
                EtcdKeys.draft(draft.id()),
                storedDraft.modificationRevision(),
                EtcdKeys.order(order.id()),
                json.write(order));
        if (!committed) {
            throw new ResourceConflictException("Draft changed, expired, or was submitted concurrently");
        }
        return order;
    }

    public Optional<Order> getOrder(UUID id) {
        return gateway.get(EtcdKeys.order(id)).map(value -> json.read(value.value(), Order.class));
    }
}
