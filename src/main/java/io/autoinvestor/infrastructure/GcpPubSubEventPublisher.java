package io.autoinvestor.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.cloud.pubsub.v1.Publisher;
import io.autoinvestor.domain.Event;
import io.autoinvestor.domain.EventPublisher;
import io.autoinvestor.exceptions.InternalErrorException;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Profile("prod")
public class GcpPubSubEventPublisher implements EventPublisher {

    private final Publisher publisher;
    private final ObjectMapper objectMapper;

    public GcpPubSubEventPublisher(
            @Value("${GCP_PROJECT}") String projectId,
            @Value("${PUBSUB_TOPIC}") String topic,
            ObjectMapper objectMapper
    ) throws Exception {
        this.objectMapper = objectMapper;
        ProjectTopicName topicName = ProjectTopicName.of(projectId, topic);
        this.publisher = Publisher.newBuilder(topicName).build();
    }

    @Override
    public void publish(List<Event<?>> events) {
        events.forEach(event -> {
            try {
                Map<String,Object> envelope = new HashMap<>();

                envelope.put("payload", event.getPayload().asMap());

                envelope.put("eventId",     event.getId().toString());
                envelope.put("type",        event.getType());
                envelope.put("aggregateId", event.getAggregateId().value());
                envelope.put("occurredAt",  Instant.ofEpochMilli(event.getOccurredAt().getTime()).toString());
                envelope.put("version",     event.getVersion());

                String data = objectMapper.writeValueAsString(envelope);
                PubsubMessage msg = PubsubMessage.newBuilder()
                        .setData(ByteString.copyFromUtf8(data))
                        .build();

                publisher.publish(msg);
            } catch (JsonProcessingException e) {
                throw new InternalErrorException("Failed to serialize event");
            }
        });
    }

    @PreDestroy
    public void shutdown() throws Exception {
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }
}
