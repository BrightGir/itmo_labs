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
import ru.bright.dto.CreateEventRequest;
import ru.bright.dto.ViewResponse;
import ru.bright.exception.ResourceNotFoundException;
import ru.bright.model.Event;
import ru.bright.service.EventService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@Valid @RequestBody CreateEventRequest request) {
        return service.createEvent(request.title(), request.type(), request.startsAt(), request.capacity());
    }

    @GetMapping
    public List<Event> listEvents() {
        return service.listEvents();
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable UUID id) {
        return service.getEvent(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    @PostMapping("/{id}/views")
    public ViewResponse registerView(@PathVariable UUID id) {
        return new ViewResponse(id, service.registerView(id));
    }

    @GetMapping("/{id}/views")
    public ViewResponse getViews(@PathVariable UUID id) {
        return new ViewResponse(id, service.getViews(id));
    }
}
