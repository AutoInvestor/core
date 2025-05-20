package io.autoinvestor.application;

public record RegisterAssetResponse(
        String assetId,
        String mic,
        String ticker,
        String name
) {}
