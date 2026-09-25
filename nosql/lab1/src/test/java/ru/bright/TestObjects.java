package ru.bright;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.bright.repository.JsonConverter;

public final class TestObjects {
    private TestObjects() {}

    public static JsonConverter jsonConverter() {
        return new JsonConverter(new ObjectMapper().findAndRegisterModules());
    }
}
