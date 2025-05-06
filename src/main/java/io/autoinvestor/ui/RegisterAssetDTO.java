package io.autoinvestor.ui;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public record RegisterAssetDTO(String mic, String ticker) {

    @JsonCreator
    public RegisterAssetDTO(@JsonProperty("mic") String mic,
                            @JsonProperty("ticker") String ticker) {
        if (mic == null || ticker == null) {
            throw new InvalidRegisterAssetInputException("All fields should not be null");
        }

        if (mic.trim().isEmpty() || ticker.trim().isEmpty()) {
            throw new InvalidRegisterAssetInputException("All fields should not be empty");
        }

        this.mic = mic;
        this.ticker = ticker;
    }
}