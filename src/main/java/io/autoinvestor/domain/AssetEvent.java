package io.autoinvestor.domain;

import java.time.Instant;
import java.util.UUID;

public interface AssetEvent<Payload> {
    UUID eventId();
    Instant occurredAt();
    UUID aggregateId();
    int version();
    String type();
    Payload payload();
}
