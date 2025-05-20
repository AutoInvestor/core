package io.autoinvestor.ui;

import io.autoinvestor.application.GetAssetCommand;
import io.autoinvestor.application.GetAssetCommandHandler;
import io.autoinvestor.application.GetAssetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class GetAssetController {

    private final GetAssetCommandHandler handler;

    public GetAssetController(GetAssetCommandHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<AssetDTO> getAsset(@PathVariable String assetId) {
        GetAssetResponse response = handler.handle(new GetAssetCommand(assetId));
        AssetDTO dto = new AssetDTO(
                response.assetId(),
                response.mic(),
                response.ticker(),
                response.name()
        );
        return ResponseEntity.ok(dto);
    }
}
