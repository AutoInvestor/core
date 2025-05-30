package io.autoinvestor.infrastructure.repositories;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "assets")
public record AssetDocument(
    @Id String assetId,
    String mic,
    String ticker,
    String name,
    @Field("occurredAt") Instant occurredAt,
    Instant updatedAt) {}
