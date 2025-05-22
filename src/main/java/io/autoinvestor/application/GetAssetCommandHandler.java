package io.autoinvestor.application;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetId;
import io.autoinvestor.domain.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class GetAssetCommandHandler {

    private final AssetRepository repository;

    public GetAssetCommandHandler(AssetRepository repository) {
        this.repository = repository;
    }

    public GetAssetResponse handle(GetAssetCommand command) {
        Optional<Asset> asset = repository.findById(AssetId.of(command.assetId()));

        if (asset.isEmpty()) {
            throw new AssetNotFoundException("Asset not found with ID: " + command.assetId());
        }

        return new GetAssetResponse(
                asset.get().id(),
                asset.get().mic(),
                asset.get().ticker(),
                asset.get().name()
        );
    }
}
