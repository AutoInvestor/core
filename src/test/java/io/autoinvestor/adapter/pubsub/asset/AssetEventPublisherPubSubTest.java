package io.autoinvestor.adapter.pubsub.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import io.autoinvestor.BaseTest;
import io.autoinvestor.domain.AssetEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AssetEventPublisherPubSubTest extends BaseTest {

    @Autowired
    private AssetEventPublisherPubSub pubSub;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void eventsArePublished() throws IOException {
        var event = new TestAssetEvent("TEST_EVENT", new TestAssetEvent.Payload("value1", 34));

        pubSub.send(event);

        List<AcknowledgeablePubsubMessage> msgs = getMessages("test-to-core", 1);

        assertEquals(1, msgs.size());

        var msgData = msgs.getFirst().getPubsubMessage().getData().toByteArray();
        var receivedMsg = objectMapper.readValue(msgData, TestAssetEvent.class);

        assertEquals(event, receivedMsg);

        for (AcknowledgeablePubsubMessage msg : msgs) {
            msg.ack();
        }
    }

    private record TestAssetEvent(String type, Payload payload) implements AssetEvent<TestAssetEvent.Payload> {
        @Override
        public UUID eventId() {
            return UUID.randomUUID();
        }

        @Override
        public Instant occurredAt() {
            return Instant.now();
        }

        @Override
        public UUID aggregateId() {
            return UUID.randomUUID();
        }

        @Override
        public int version() {
            return 0;
        }

        record Payload(String property1, Integer property2) {
        }
    }
}
