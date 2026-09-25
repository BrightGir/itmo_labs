package ru.bright.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.bright.exception.StorageUnavailableException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class JetcdEtcdGateway implements EtcdGateway {
    private final KV keyValueClient;
    private final Lease leaseClient;
    private final long timeoutMillis;

    public JetcdEtcdGateway(Client client, @Value("${app.etcd-timeout-millis:3000}") long timeoutMillis) {
        this.keyValueClient = client.getKVClient();
        this.leaseClient = client.getLeaseClient();
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public Optional<StoredValue> get(String key) {
        try {
            List<KeyValue> values = keyValueClient.get(bytes(key))
                    .get(timeoutMillis, TimeUnit.MILLISECONDS)
                    .getKvs();
            return values.stream().findFirst().map(this::toStoredValue);
        } catch (Exception exception) {
            throw new StorageUnavailableException("Etcd operation failed", exception);
        }
    }

    @Override
    public List<StoredValue> getPrefix(String prefix) {
        try {
            GetOption option = GetOption.builder().isPrefix(true).build();
            return keyValueClient.get(bytes(prefix), option)
                    .get(timeoutMillis, TimeUnit.MILLISECONDS)
                    .getKvs().stream()
                    .map(this::toStoredValue)
                    .toList();
        } catch (Exception exception) {
            throw new StorageUnavailableException("Etcd operation failed", exception);
        }
    }

    @Override
    public void put(String key, String value) {
        try {
            keyValueClient.put(bytes(key), bytes(value)).get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            throw new StorageUnavailableException("Etcd operation failed", exception);
        }
    }

    @Override
    public void putWithTtl(String key, String value, long ttlSeconds) {
        try {
            long leaseId = leaseClient.grant(ttlSeconds).get(timeoutMillis, TimeUnit.MILLISECONDS).getID();
            PutOption option = PutOption.builder().withLeaseId(leaseId).build();
            keyValueClient.put(bytes(key), bytes(value), option).get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            throw new StorageUnavailableException("Etcd operation failed", exception);
        }
    }

    @Override
    public boolean compareAndSet(String key, long expectedRevision, String value) {
        try {
            Cmp revisionMatches = new Cmp(bytes(key), Cmp.Op.EQUAL, CmpTarget.modRevision(expectedRevision));
            TxnResponse response = keyValueClient.txn()
                    .If(revisionMatches)
                    .Then(Op.put(bytes(key), bytes(value), PutOption.DEFAULT))
                    .commit()
                    .get(timeoutMillis, TimeUnit.MILLISECONDS);
            return response.isSucceeded();
        } catch (Exception exception) {
            throw new StorageUnavailableException("Etcd operation failed", exception);
        }
    }

    @Override
    public boolean createOrderAndDeleteDraft(String eventKey, String draftKey, long draftRevision,
                                              String orderKey, String orderValue) {
        try {
            Cmp eventExists = new Cmp(bytes(eventKey), Cmp.Op.GREATER, CmpTarget.version(0));
            Cmp draftUnchanged = new Cmp(bytes(draftKey), Cmp.Op.EQUAL, CmpTarget.modRevision(draftRevision));
            TxnResponse response = keyValueClient.txn()
                    .If(eventExists, draftUnchanged)
                    .Then(Op.put(bytes(orderKey), bytes(orderValue), PutOption.DEFAULT),
                            Op.delete(bytes(draftKey), DeleteOption.DEFAULT))
                    .commit()
                    .get(timeoutMillis, TimeUnit.MILLISECONDS);
            return response.isSucceeded();
        } catch (Exception exception) {
            throw new StorageUnavailableException("Etcd operation failed", exception);
        }
    }

    @Override
    public void delete(String key) {
        try {
            keyValueClient.delete(bytes(key)).get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            throw new StorageUnavailableException("Etcd operation failed", exception);
        }
    }

    private StoredValue toStoredValue(KeyValue value) {
        return new StoredValue(value.getValue().toString(StandardCharsets.UTF_8), value.getModRevision());
    }

    private static ByteSequence bytes(String value) {
        return ByteSequence.from(value, StandardCharsets.UTF_8);
    }
}
