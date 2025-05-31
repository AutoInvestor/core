package io.autoinvestor.application;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetRepository;
import io.autoinvestor.domain.EventPublisher;

import org.springframework.stereotype.Service;

@Service
public class RegisterAssetCommandHandler {

    private final AssetRepository repository;
    private final EventPublisher eventPublisher;

    public RegisterAssetCommandHandler(AssetRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public RegisterAssetResponse handle(RegisterAssetCommand command) {
        if (this.repository.exists(command.mic(), command.ticker())) {
            throw new AssetAlreadyExists(
                    "Duplicated asset for this mic: "
                            + command.mic()
                            + " and ticker: "
                            + command.ticker());
        }

        Asset asset = Asset.create(command.mic(), command.ticker(), command.name());

        this.repository.save(asset);
        this.eventPublisher.publish(asset.releaseEvents());

        return new RegisterAssetResponse(asset.id(), asset.mic(), asset.ticker(), asset.name());
    }
}
