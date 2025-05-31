package io.autoinvestor.application;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetId;
import io.autoinvestor.domain.AssetPriceFetcher;
import io.autoinvestor.domain.AssetRepository;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class GetAssetPriceCommandHandler {

    private final AssetPriceFetcher fetcher;
    private final AssetRepository repository;

    public GetAssetPriceCommandHandler(AssetRepository repository, AssetPriceFetcher fetcher) {
        this.repository = repository;
        this.fetcher = fetcher;
    }

    public GetAssetPriceResponse handle(GetAssetPriceCommand command) {
        Optional<Asset> asset = repository.findById(AssetId.of(command.assetId()));

        if (asset.isEmpty()) {
            throw new AssetNotFoundException("Asset not found with ID: " + command.assetId());
        }

        float price = fetcher.priceOn(asset.get(), command.date());
        return new GetAssetPriceResponse(price, command.date());
    }
}
