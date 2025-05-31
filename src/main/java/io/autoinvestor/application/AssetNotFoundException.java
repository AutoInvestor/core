package io.autoinvestor.application;

import io.autoinvestor.exceptions.BadRequestException;

public class AssetNotFoundException extends BadRequestException {
    public AssetNotFoundException(String message) {
        super(message);
    }
}
