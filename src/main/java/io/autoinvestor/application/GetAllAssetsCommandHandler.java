package io.autoinvestor.application;

import io.autoinvestor.domain.Asset;
import io.autoinvestor.domain.AssetRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class GetAllAssetsCommandHandler {

  private final AssetRepository repository;

  public GetAllAssetsCommandHandler(AssetRepository repository) {
    this.repository = repository;
  }

  public List<GetAssetResponse> handle() {
    List<Asset> assets = this.repository.findAll();
    return assets.stream()
        .map(asset -> new GetAssetResponse(asset.id(), asset.mic(), asset.ticker(), asset.name()))
        .collect(Collectors.toList());
  }
}
