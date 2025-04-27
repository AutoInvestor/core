package io.autoinvestor;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PubSubTestConfiguration {

    @Bean
    public CredentialsProvider googleCredentials() {
        return NoCredentialsProvider.create();
    }
}
