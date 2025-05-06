package io.autoinvestor.ui;

import io.autoinvestor.application.RegisterAssetCommand;
import io.autoinvestor.application.RegisterAssetCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/register")
public class RegisterAssetController {

    private final RegisterAssetCommandHandler commandHandler;

    public RegisterAssetController(RegisterAssetCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PostMapping
    public ResponseEntity<Void> handle(@RequestBody RegisterAssetDTO dto) {
        this.commandHandler.handle(new RegisterAssetCommand(
                dto.mic(), dto.ticker()
        ));
        return ResponseEntity.ok().build();
    }
}
