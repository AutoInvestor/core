package io.autoinvestor.application;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetId;
import io.autoinvestor.domain.AssetRepository;
import org.springframework.stereotype.Service;


@Service
public class GetAssetCommandHandler {

    private final AssetRepository repository;

    public GetAssetCommandHandler(AssetRepository repository) {
        this.repository = repository;
    }

    public GetAssetResponse handle(GetAssetCommand command) {
        Asset asset = repository.findById(AssetId.of(command.assetId()))
                .orElseThrow(() -> new AssetNotFoundException(
                        "Asset not found with id: " + command.assetId()));

        return new GetAssetResponse(
                asset.id(),
                asset.mic(),
                asset.ticker(),
                asset.name()
        );
    }
}
