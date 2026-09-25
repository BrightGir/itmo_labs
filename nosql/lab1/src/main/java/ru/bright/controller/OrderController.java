package ru.bright.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.bright.dto.CreateDraftRequest;
import ru.bright.exception.ResourceNotFoundException;
import ru.bright.model.DraftOrder;
import ru.bright.model.Order;
import ru.bright.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public DraftOrder createDraft(@Valid @RequestBody CreateDraftRequest request) {
        return service.createDraft(request.eventId(), request.managerId(), request.quantity(),
                request.comment(), request.ttlSeconds());
    }

    @GetMapping("/drafts/{id}")
    public DraftOrder getDraft(@PathVariable UUID id) {
        return service.getDraft(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found: " + id));
    }

    @PostMapping("/drafts/{id}/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public Order submitDraft(@PathVariable UUID id) {
        return service.submitDraft(id);
    }

    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable UUID id) {
        return service.getOrder(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }
}
