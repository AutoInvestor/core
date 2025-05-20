package io.autoinvestor.domain;

public class AssetId extends Id {
    AssetId(String id) {
        super(id);
    }

    public static AssetId generate() {
        return new AssetId(generateId());
    }

    public static AssetId of(String id) {
        return new AssetId(id);
    }
}
