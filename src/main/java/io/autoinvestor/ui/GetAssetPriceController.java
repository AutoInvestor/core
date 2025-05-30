package io.autoinvestor.ui;

import io.autoinvestor.application.GetAssetPriceCommand;
import io.autoinvestor.application.GetAssetPriceCommandHandler;
import io.autoinvestor.application.GetAssetPriceResponse;

import java.time.Instant;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class GetAssetPriceController {

  private final GetAssetPriceCommandHandler handler;

  public GetAssetPriceController(GetAssetPriceCommandHandler handler) {
    this.handler = handler;
  }

  @GetMapping("/{assetId}/price")
  public ResponseEntity<PriceDTO> getPrice(
      @PathVariable String assetId,
      @RequestParam(name = "at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date at) {

    Date date = at != null ? at : Date.from(Instant.now());

    GetAssetPriceResponse response = handler.handle(new GetAssetPriceCommand(assetId, date));
    PriceDTO dto = new PriceDTO(response.date(), Math.round(response.price() * 100));
    return ResponseEntity.ok(dto);
  }
}
