package io.autoinvestor.domain;

public class AssetWasRegisteredEvent extends Event<AssetWasRegisteredEventPayload> {

    private AssetWasRegisteredEvent(Id aggregateId, AssetWasRegisteredEventPayload payload) {
        super(aggregateId, "ASSET_CREATED", payload);
    }

    public static AssetWasRegisteredEvent from(Asset asset) {
        AssetWasRegisteredEventPayload payload =
                new AssetWasRegisteredEventPayload(asset.mic(), asset.ticker(), asset.name());
        return new AssetWasRegisteredEvent(asset.getId(), payload);
    }
}
