package ru.bright;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.bright.repository.EtcdGateway;
import ru.bright.repository.InMemoryEtcdGateway;

import java.time.Clock;

class ApplicationContextTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(LibraryEtcdApplication.class, Config.class);

    @Test
    void applicationContextStarts() {
        contextRunner.run(context -> org.assertj.core.api.Assertions.assertThat(context).hasNotFailed());
    }

    @TestConfiguration
    static class Config {
        @Bean
        @Primary
        EtcdGateway testGateway(Clock clock) {
            return new InMemoryEtcdGateway(clock);
        }
    }
}
