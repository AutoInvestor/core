package io.autoinvestor.infrastructure.repositories;

import io.autoinvestor.domain.Asset;
import org.springframework.stereotype.Component;

@Component
class AssetMapper {

    AssetDocument toDocument(Asset domain) {
        return new AssetDocument(
                domain.getId().value(),
                domain.mic(),
                domain.ticker(),
                domain.name(),
                domain.getCreatedAt().toInstant(),
                domain.getUpdatedAt().toInstant());
    }

    public Asset toDomain(AssetDocument assetDocument) {
        return Asset.create(
                assetDocument.mic(),
                assetDocument.ticker(),
                assetDocument.name(),
                assetDocument.occurredAt(),
                assetDocument.updatedAt());
    }
}

