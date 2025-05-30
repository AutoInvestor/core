package io.autoinvestor.ui;

import io.autoinvestor.application.GetAllAssetsCommandHandler;
import io.autoinvestor.application.GetAssetResponse;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class GetAllAssetsController {

  private final GetAllAssetsCommandHandler handler;

  public GetAllAssetsController(GetAllAssetsCommandHandler handler) {
    this.handler = handler;
  }

  @GetMapping
  public ResponseEntity<List<AssetDTO>> getAllAssets() {
    List<GetAssetResponse> assets = handler.handle();
    List<AssetDTO> dtos =
        assets.stream()
            .map(a -> new AssetDTO(a.assetId(), a.mic(), a.ticker(), a.name()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }
}
