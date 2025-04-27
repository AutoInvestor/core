package io.autoinvestor;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;

@Testcontainers
@Import(PubSubTestConfiguration.class)
public abstract class BaseTest {

    public static final String PROJECT_ID = "test-project";

    private static final Map<String, String> TOPOLOGY = Map.of(
            "core", "test-to-core"
    );

    @Autowired
    private PubSubSubscriberTemplate subscriberTemplate;

    @Container
    public static final PubSubEmulatorContainer PUB_SUB_EMULATOR_CONTAINER =
            new PubSubEmulatorContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:317.0.0-emulators"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.gcp.pubsub.project-id", () -> PROJECT_ID);
        registry.add("spring.cloud.gcp.pubsub.emulator-host", PUB_SUB_EMULATOR_CONTAINER::getEmulatorEndpoint);
    }

    @BeforeAll
    static void setup() throws Exception {
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget("dns:///" + PUB_SUB_EMULATOR_CONTAINER.getEmulatorEndpoint())
                .usePlaintext()
                .build();

        var channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

        TopicAdminClient topicAdminClient = TopicAdminClient.create(
                TopicAdminSettings.newBuilder()
                        .setCredentialsProvider(NoCredentialsProvider.create())
                        .setTransportChannelProvider(channelProvider)
                        .build());

        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(
                SubscriptionAdminSettings.newBuilder()
                        .setTransportChannelProvider(channelProvider)
                        .setCredentialsProvider(NoCredentialsProvider.create())
                        .build());

        try (var admin = new PubSubAdmin(() -> PROJECT_ID, topicAdminClient, subscriptionAdminClient)) {
            TOPOLOGY.keySet().forEach(admin::createTopic);
            TOPOLOGY.forEach((topic, subscription) -> admin.createSubscription(subscription, topic));
        } finally {
            channel.shutdown();
        }
    }

    protected List<AcknowledgeablePubsubMessage> getMessages(String subscription, int amount) {
        var list = new ArrayList<AcknowledgeablePubsubMessage>();
        await().until(() -> {
            var messages = subscriberTemplate.pull(subscription, amount, true);
            list.addAll(messages);
            return messages;
        }, retrievedMessages -> list.size() == amount);
        return list;
    }

    @AfterEach
    void teardown() {
        TOPOLOGY.values().stream().distinct().forEach(subscription ->
                await().until(() -> subscriberTemplate.pullAndAck(subscription, 1000, true), hasSize(0)));
    }
}

