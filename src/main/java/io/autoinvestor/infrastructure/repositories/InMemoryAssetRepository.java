package io.autoinvestor.infrastructure.repositories;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetId;
import io.autoinvestor.domain.AssetRepository;

import java.util.*;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
public class InMemoryAssetRepository implements AssetRepository {
    private final Map<String, Asset> assetStore = new HashMap<>();

    @Override
    public void save(Asset asset) {
        assetStore.put(asset.id(), asset);
    }

    @Override
    public boolean exists(String mic, String ticker) {
        return assetStore.values().stream()
                .anyMatch(asset -> asset.mic().equals(mic) && asset.ticker().equals(ticker));
    }

    @Override
    public Optional<Asset> findById(AssetId assetId) {
        return Optional.ofNullable(assetStore.get(assetId.value()));
    }

    @Override
    public List<Asset> findAll() {
        return new ArrayList<>(assetStore.values());
    }

    public void clear() {
        assetStore.clear();
    }
}
