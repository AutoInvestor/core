package io.autoinvestor.domain;

public interface AssetRepository {
    void save(Asset asset);
    boolean exists(String mic, String ticker);
}
