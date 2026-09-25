package ru.bright.config;

import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean(destroyMethod = "close")
    Client etcdClient(@Value("${app.etcd.endpoints:http://localhost:2379}") String endpoints) {
        return Client.builder().endpoints(endpoints.split(",")).build();
    }
}
