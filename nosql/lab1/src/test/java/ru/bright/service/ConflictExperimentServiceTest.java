package ru.bright.service;

import org.junit.jupiter.api.Test;
import ru.bright.dto.ConflictExperimentResponse;
import ru.bright.repository.InMemoryEtcdGateway;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

class ConflictExperimentServiceTest {
    @Test
    void casPreservesAllIncrementsAfterParallelStart() {
        ConflictExperimentService service = new ConflictExperimentService(new InMemoryEtcdGateway(Clock.systemUTC()));

        ConflictExperimentResponse result = service.run(24);

        assertThat(result.writers()).isEqualTo(24);
        assertThat(result.blindWriteFinalValue()).isBetween(1L, 24L);
        assertThat(result.casFinalValue()).isEqualTo(24);
    }
}
