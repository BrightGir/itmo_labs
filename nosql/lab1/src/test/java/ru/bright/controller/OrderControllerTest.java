package ru.bright.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.bright.TestObjects;
import ru.bright.exception.ApiExceptionHandler;
import ru.bright.model.Event;
import ru.bright.repository.EtcdKeys;
import ru.bright.repository.InMemoryEtcdGateway;
import ru.bright.repository.JsonConverter;
import ru.bright.service.EventService;
import ru.bright.service.OrderService;
import ru.bright.service.SettingsService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {
    @Test
    void usesManagerDefaultQuantityWhenDraftOmitsIt() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-09-12T12:00:00Z"), ZoneOffset.UTC);
        InMemoryEtcdGateway gateway = new InMemoryEtcdGateway(clock);
        JsonConverter json = TestObjects.jsonConverter();
        Event event = new EventService(gateway, json, clock)
                .createEvent("Book presentation", "PRESENTATION", clock.instant(), 50);
        gateway.put(EtcdKeys.settings("manager-1"), "{\"managerId\":\"manager-1\",\"defaultQuantity\":4}");
        MockMvc mvc = MockMvcBuilders.standaloneSetup(
                        new OrderController(new OrderService(gateway, json, clock,
                                new SettingsService(gateway, json, clock, 60))))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();

        mvc.perform(post("/api/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventId":"%s","managerId":"manager-1","ttlSeconds":30}
                                """.formatted(event.id())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(4));
    }
}
