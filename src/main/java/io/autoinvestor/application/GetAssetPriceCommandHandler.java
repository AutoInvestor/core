package io.autoinvestor.application;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetId;
import io.autoinvestor.domain.AssetPriceFetcher;
import io.autoinvestor.domain.AssetRepository;
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
        Asset asset = repository.findById(AssetId.of(command.assetId()))
                .orElseThrow(() -> new AssetNotFoundException("Asset not found with id: " + command.assetId()));

        float price = fetcher.priceOn(asset, command.date());
        return new GetAssetPriceResponse(price, command.date());
    }
}
