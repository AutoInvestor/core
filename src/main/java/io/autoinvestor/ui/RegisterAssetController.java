package io.autoinvestor.ui;

import io.autoinvestor.application.RegisterAssetCommand;
import io.autoinvestor.application.RegisterAssetCommandHandler;
import io.autoinvestor.application.RegisterAssetResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assets")
public class RegisterAssetController {

  private final RegisterAssetCommandHandler commandHandler;

  public RegisterAssetController(RegisterAssetCommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  @PostMapping
  public ResponseEntity<AssetDTO> handle(@RequestBody RegisterAssetDTO queryDto) {
    RegisterAssetResponse response =
        this.commandHandler.handle(
            new RegisterAssetCommand(queryDto.mic(), queryDto.ticker(), queryDto.name()));
    AssetDTO dto =
        new AssetDTO(response.assetId(), response.mic(), response.ticker(), response.name());
    return ResponseEntity.ok(dto);
  }
}
