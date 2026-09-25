package ru.bright.service;

import org.springframework.stereotype.Service;
import ru.bright.dto.ConflictExperimentResponse;
import ru.bright.repository.EtcdGateway;
import ru.bright.repository.EtcdKeys;
import ru.bright.repository.StoredValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ConflictExperimentService {
    private final EtcdGateway gateway;

    public ConflictExperimentService(EtcdGateway gateway) {
        this.gateway = gateway;
    }

    public ConflictExperimentResponse run(int writers) {
        if (writers < 2 || writers > 100) {
            throw new IllegalArgumentException("writers must be 2..100");
        }

        gateway.delete(EtcdKeys.CONFLICT);
        runTogether(writers, () -> {
            long oldValue = currentValue();
            gateway.put(EtcdKeys.CONFLICT, Long.toString(oldValue + 1));
        });
        long blindWriteResult = currentValue();

        gateway.delete(EtcdKeys.CONFLICT);
        AtomicLong conflicts = new AtomicLong();
        runTogether(writers, () -> incrementWithCas(conflicts));

        return new ConflictExperimentResponse(writers, blindWriteResult, currentValue(), conflicts.get());
    }

    private void incrementWithCas(AtomicLong conflicts) {
        while (true) {
            Optional<StoredValue> stored = gateway.get(EtcdKeys.CONFLICT);
            long oldValue = stored.map(value -> Long.parseLong(value.value())).orElse(0L);
            long revision = stored.map(StoredValue::modificationRevision).orElse(0L);
            if (gateway.compareAndSet(EtcdKeys.CONFLICT, revision, Long.toString(oldValue + 1))) {
                return;
            }
            conflicts.incrementAndGet();
        }
    }

    private void runTogether(int writers, Runnable action) {
        ExecutorService pool = Executors.newFixedThreadPool(writers);
        CountDownLatch ready = new CountDownLatch(writers);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<?>> tasks = new ArrayList<>();
            for (int i = 0; i < writers; i++) {
                tasks.add(pool.submit(() -> {
                    ready.countDown();
                    await(start);
                    action.run();
                }));
            }
            await(ready);
            start.countDown();
            for (Future<?> task : tasks) {
                task.get();
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Parallel experiment failed", exception);
        } finally {
            pool.shutdownNow();
        }
    }

    private long currentValue() {
        return gateway.get(EtcdKeys.CONFLICT)
                .map(value -> Long.parseLong(value.value()))
                .orElse(0L);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
