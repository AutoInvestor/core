package io.autoinvestor.adapter.pubsub.asset;

import io.autoinvestor.domain.AssetEvent;
import io.autoinvestor.port.AssetEventPublisher;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class AssetEventPublisherPubSub implements AssetEventPublisher {

    private final MessageChannel core;

    public AssetEventPublisherPubSub(@Qualifier(AssetEventOutboundAdapterPubSub.PUBSUB_CHANNEL) MessageChannel core) {
        this.core = core;
    }

    @Override
    public <T> void send(AssetEvent<T> event) {
        this.core.send(new AssetEventMessage<>(event));
    }

    private record AssetEventMessage<Payload>(AssetEvent<Payload> assetEvent) implements Message<AssetEvent<Payload>> {

        @Override
        @Nonnull
        public AssetEvent<Payload> getPayload() {
            return assetEvent;
        }

        @Override
        @Nonnull
        public MessageHeaders getHeaders() {
            return new MessageHeaders(Map.of("EVENT_TYPE", assetEvent.type()));
        }
    }
}
