package io.autoinvestor.ui;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterAssetDTO(String mic, String ticker, String name) {

    @JsonCreator
    public RegisterAssetDTO(
            @JsonProperty("mic") String mic,
            @JsonProperty("ticker") String ticker,
            @JsonProperty("name") String name) {
        if (mic == null
                || ticker == null
                || name == null
                || mic.isEmpty()
                || ticker.isEmpty()
                || name.isEmpty()) {
            throw new InvalidRegisterAssetInputException("All fields should not be null");
        }

        if (mic.trim().isEmpty() || ticker.trim().isEmpty() || name.trim().isEmpty()) {
            throw new InvalidRegisterAssetInputException("All fields should not be empty");
        }

        this.mic = mic;
        this.ticker = ticker;
        this.name = name;
    }
}
