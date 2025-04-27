package io.autoinvestor.adapter.pubsub.asset;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Slf4j
@Configuration
@RequiredArgsConstructor
class AssetEventOutboundAdapterPubSub {

    static final String PUBSUB_CHANNEL = "core";

    @Bean(PUBSUB_CHANNEL)
    public MessageChannel core() {
        return new PublishSubscribeChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = PUBSUB_CHANNEL)
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
        PubSubMessageHandler adapter = new PubSubMessageHandler(pubsubTemplate, PUBSUB_CHANNEL);

        adapter.setSuccessCallback(((ackId, message) -> log.info("Message was sent via the outbound channel adapter to topic-two!")));

        adapter.setFailureCallback((cause, message) -> log.info("Error sending {}", message, cause));

        return adapter;
    }
}
