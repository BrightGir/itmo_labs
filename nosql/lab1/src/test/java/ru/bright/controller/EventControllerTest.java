package ru.bright.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.bright.TestObjects;
import ru.bright.exception.ApiExceptionHandler;
import ru.bright.repository.InMemoryEtcdGateway;
import ru.bright.service.EventService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-12T12:00:00Z"), ZoneOffset.UTC);
        EventService service = new EventService(
                new InMemoryEtcdGateway(clock), TestObjects.jsonConverter(), clock);
        mvc = MockMvcBuilders.standaloneSetup(new EventController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void createsLibraryEvent() throws Exception {
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Book presentation","type":"PRESENTATION",
                                 "startsAt":"2026-09-13T12:00:00Z","capacity":50}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Book presentation"))
                .andExpect(jsonPath("$.capacity").value(50));
    }

    @Test
    void rejectsInvalidEvent() throws Exception {
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"type\":\"\",\"capacity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"));
    }
}
