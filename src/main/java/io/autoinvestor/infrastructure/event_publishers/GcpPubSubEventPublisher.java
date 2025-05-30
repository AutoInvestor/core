package io.autoinvestor.infrastructure.event_publishers;

import io.autoinvestor.domain.Event;
import io.autoinvestor.domain.EventPublisher;
import jakarta.annotation.PreDestroy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;

@Component
@Profile("prod")
public class GcpPubSubEventPublisher implements EventPublisher {

  private final Publisher publisher;
  private final EventMessageMapper mapper;

  public GcpPubSubEventPublisher(
      @Value("${GCP_PROJECT}") String projectId,
      @Value("${PUBSUB_TOPIC}") String topic,
      ObjectMapper objectMapper)
      throws Exception {
    this.mapper = new EventMessageMapper(objectMapper);
    ProjectTopicName topicName = ProjectTopicName.of(projectId, topic);
    this.publisher = Publisher.newBuilder(topicName).build();
  }

  @Override
  public void publish(List<Event<?>> events) {
    events.stream().map(mapper::toMessage).forEach(publisher::publish);
  }

  @PreDestroy
  public void shutdown() throws Exception {
    publisher.shutdown();
    publisher.awaitTermination(1, TimeUnit.MINUTES);
  }
}
