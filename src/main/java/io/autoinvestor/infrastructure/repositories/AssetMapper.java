package io.autoinvestor.infrastructure.repositories;

import io.autoinvestor.domain.Asset;

import java.util.Date;

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
    return Asset.from(
        assetDocument.assetId(),
        assetDocument.mic(),
        assetDocument.ticker(),
        assetDocument.name(),
        Date.from(assetDocument.occurredAt()),
        Date.from(assetDocument.updatedAt()));
  }
}
