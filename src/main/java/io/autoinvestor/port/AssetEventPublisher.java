package io.autoinvestor.port;

import io.autoinvestor.domain.AssetEvent;

public interface AssetEventPublisher {
    <T> void send(AssetEvent<T> event);
}
