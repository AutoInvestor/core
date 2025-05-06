package io.autoinvestor.domain;

import java.util.Map;

public record AssetWasRegisteredEventPayload(String mic, String ticker) implements EventPayload {
    @Override
    public Map<String, Object> asMap() {
        return Map.of("mic", ticker, "ticker", ticker);
    }
}
