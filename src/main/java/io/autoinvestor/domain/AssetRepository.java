package io.autoinvestor.domain;


import java.util.Optional;

public interface AssetRepository {
    void save(Asset asset);
    boolean exists(String mic, String ticker);
    Optional<Asset> findById(AssetId assetId);
}
