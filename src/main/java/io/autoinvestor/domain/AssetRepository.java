package io.autoinvestor.domain;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {
    void save(Asset asset);

    boolean exists(String mic, String ticker);

    Optional<Asset> findById(AssetId assetId);

    List<Asset> findAll();
}
