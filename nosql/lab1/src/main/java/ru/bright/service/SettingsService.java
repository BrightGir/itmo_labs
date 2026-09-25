package ru.bright.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.bright.model.ManagerSettings;
import ru.bright.repository.EtcdGateway;
import ru.bright.repository.EtcdKeys;
import ru.bright.repository.JsonConverter;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SettingsService {
    private static final int MAX_CACHE_SIZE = 1_000;

    private record CacheEntry(ManagerSettings settings, Instant expiresAt) {}

    private final EtcdGateway gateway;
    private final JsonConverter json;
    private final Clock clock;
    private final long cacheTtlSeconds;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public SettingsService(EtcdGateway gateway, JsonConverter json, Clock clock,
                           @Value("${app.settings-cache-ttl-seconds:60}") long cacheTtlSeconds) {
        this.gateway = gateway;
        this.json = json;
        this.clock = clock;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public void saveSettings(ManagerSettings settings) {
        gateway.put(EtcdKeys.settings(settings.managerId()), json.write(settings));
        cache.remove(settings.managerId());
    }

    public Optional<ManagerSettings> getSettings(String managerId) {
        Instant now = clock.instant();
        CacheEntry cached = cache.get(managerId);
        if (cached != null && cached.expiresAt().isAfter(now)) {
            return Optional.of(cached.settings());
        }

        Optional<ManagerSettings> loaded = gateway.get(EtcdKeys.settings(managerId))
                .map(value -> json.read(value.value(), ManagerSettings.class));
        loaded.ifPresent(settings -> cache(settings, now));
        return loaded;
    }

    private void cache(ManagerSettings settings, Instant now) {
        if (cache.size() >= MAX_CACHE_SIZE) {
            cache.clear();
        }
        cache.put(settings.managerId(), new CacheEntry(settings, now.plusSeconds(cacheTtlSeconds)));
    }
}
