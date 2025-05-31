package io.autoinvestor.ui;

import com.fasterxml.jackson.annotation.JsonFormat;

public record AssetDTO(
        @JsonFormat(shape = JsonFormat.Shape.STRING) String assetId,
        String mic,
        String ticker,
        String name) {}
