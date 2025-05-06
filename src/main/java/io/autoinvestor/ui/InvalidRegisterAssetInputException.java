package io.autoinvestor.ui;

import io.autoinvestor.exceptions.BadRequestException;

public class InvalidRegisterAssetInputException extends BadRequestException {
    public InvalidRegisterAssetInputException(String message) {
        super(message);
    }
}
