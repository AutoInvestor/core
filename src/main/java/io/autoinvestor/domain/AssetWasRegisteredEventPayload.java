package io.autoinvestor.domain;

import java.util.Map;

public record AssetWasRegisteredEventPayload(String mic, String ticker, String name)
    implements EventPayload {
  @Override
  public Map<String, Object> asMap() {
    return Map.of("mic", mic, "ticker", ticker, "name", name);
  }
}
