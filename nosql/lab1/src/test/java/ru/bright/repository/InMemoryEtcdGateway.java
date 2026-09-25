package ru.bright.repository;

import ru.bright.TestObjects;
import ru.bright.model.ManagerSettings;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryEtcdGateway implements EtcdGateway {
    private record Entry(String value, long revision, Instant expiresAt) {}

    private final Object lock = new Object();
    private final Map<String, Entry> values = new HashMap<>();
    private final AtomicLong revision = new AtomicLong();
    private final JsonConverter json = TestObjects.jsonConverter();
    private Instant now;

    public InMemoryEtcdGateway(Clock clock) {
        this.now = clock.instant();
    }

    public void advanceSeconds(long seconds) {
        now = now.plusSeconds(seconds);
    }

    public void overwriteSettings(String managerId, ManagerSettings settings) {
        put(EtcdKeys.settings(managerId), json.write(settings));
    }

    @Override
    public Optional<StoredValue> get(String key) {
        synchronized (lock) {
            Entry entry = values.get(key);
            if (entry != null && entry.expiresAt() != null && !entry.expiresAt().isAfter(now)) {
                values.remove(key);
                entry = null;
            }
            return entry == null
                    ? Optional.empty()
                    : Optional.of(new StoredValue(entry.value(), entry.revision()));
        }
    }

    @Override
    public List<StoredValue> getPrefix(String prefix) {
        synchronized (lock) {
            List<StoredValue> result = new ArrayList<>();
            values.keySet().stream()
                    .filter(key -> key.startsWith(prefix))
                    .forEach(key -> get(key).ifPresent(result::add));
            return result;
        }
    }

    @Override
    public void put(String key, String value) {
        synchronized (lock) {
            values.put(key, new Entry(value, revision.incrementAndGet(), null));
        }
    }

    @Override
    public void putWithTtl(String key, String value, long ttlSeconds) {
        synchronized (lock) {
            values.put(key, new Entry(value, revision.incrementAndGet(), now.plusSeconds(ttlSeconds)));
        }
    }

    @Override
    public boolean compareAndSet(String key, long expectedRevision, String value) {
        synchronized (lock) {
            Entry current = values.get(key);
            long actualRevision = current == null ? 0 : current.revision();
            if (actualRevision != expectedRevision) {
                return false;
            }
            values.put(key, new Entry(value, revision.incrementAndGet(), null));
            return true;
        }
    }

    @Override
    public boolean createOrderAndDeleteDraft(String eventKey, String draftKey, long draftRevision,
                                              String orderKey, String orderValue) {
        synchronized (lock) {
            Entry event = values.get(eventKey);
            Entry draft = values.get(draftKey);
            if (event == null || draft == null || draft.revision() != draftRevision) {
                return false;
            }
            values.put(orderKey, new Entry(orderValue, revision.incrementAndGet(), null));
            values.remove(draftKey);
            return true;
        }
    }

    @Override
    public void delete(String key) {
        synchronized (lock) {
            values.remove(key);
        }
    }
}
