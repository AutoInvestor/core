package io.autoinvestor.infrastructure.repositories;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Profile("local")
public class InMemoryAssetRepository implements AssetRepository {
    private final Map<String, Asset> assetStore = new HashMap<>();

    @Override
    public void save(Asset asset) {
        String key = asset.mic() + ":" + asset.ticker();
        assetStore.put(key, asset);
    }

    @Override
    public boolean exists(String mic, String ticker) {
        return assetStore.containsKey(mic + ":" + ticker);
    }

    public void clear() {
        assetStore.clear();
    }
}
